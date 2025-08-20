package org.aptech.carBidding.services.implementation;

import lombok.RequiredArgsConstructor;
import org.aptech.carBidding.dtos.requests.BidRequest;
import org.aptech.carBidding.dtos.requests.CreateAuctionRequest;
import org.aptech.carBidding.dtos.response.AuctionDetailResponse;
import org.aptech.carBidding.dtos.response.AuctionListResponse;
import org.aptech.carBidding.dtos.response.BidResponse;
import org.aptech.carBidding.models.Auction;
import org.aptech.carBidding.models.Bids;
import org.aptech.carBidding.models.Car;
import org.aptech.carBidding.models.User;
import org.aptech.carBidding.repository.AuctionRepository;
import org.aptech.carBidding.repository.BidsRepository;
import org.aptech.carBidding.repository.CarRepository;
import org.aptech.carBidding.repository.UserRepository;
import org.aptech.carBidding.services.AuctionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepository auctionRepo;
    private final CarRepository     carRepo;
    private final BidsRepository    bidsRepo;
    private final UserRepository userRepo;

    @Override
    public List<AuctionListResponse> listAuctions() {
        return auctionRepo.findByIsClosedFalse().stream()
                .map(this::toListDto)
                .collect(Collectors.toList());
    }

    @Override
    public AuctionDetailResponse getAuctionById(Long id) {
        Auction auc = auctionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Auction not found"));
        List<BidResponse> bidHistory = bidsRepo
                .findByAuctionOrderByAmountDesc(auc)
                .stream()
                .map(this::toBidDto)
                .collect(Collectors.toList());
        return toDetailDto(auc, bidHistory);
    }

    @Override
    public AuctionDetailResponse createAuction(CreateAuctionRequest req, String sellerEmail) {
        Car car = carRepo.findById(req.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found"));
        if (!car.getOwner().getEmail().equals(sellerEmail)) {
            throw new RuntimeException("You don’t own that car");
        }
        Auction a = Auction.builder()
                .car(car)
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .startingPrice(req.getStartingPrice())
                .isClosed(false)
                .build();
        auctionRepo.save(a);
        return toDetailDto(a, List.of());
    }

    @Override
    public AuctionDetailResponse updateAuction(Long id, CreateAuctionRequest req, String sellerEmail) {
        Auction a = auctionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Auction not found"));
        if (!a.getCar().getOwner().getEmail().equals(sellerEmail)) {
            throw new RuntimeException("Not your auction");
        }
        a.setStartTime(req.getStartTime());
        a.setEndTime(req.getEndTime());
        a.setStartingPrice(req.getStartingPrice());
        auctionRepo.save(a);
        return getAuctionById(id);
    }

    @Override
    public void deleteAuction(Long id, String sellerEmail) {
        Auction a = auctionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Auction not found"));
        if (!a.getCar().getOwner().getEmail().equals(sellerEmail)) {
            throw new RuntimeException("Not your auction");
        }
        auctionRepo.delete(a);
    }

    @Override
    @Transactional
    public AuctionDetailResponse placeBid(Long auctionId, BidRequest bidReq, String bidderEmail) {
        Auction auction = auctionRepo.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        if (auction.getIsClosed()) {
            throw new RuntimeException("Cannot bid on a closed auction");
        }

        // Fetch the bidding user
        User bidder = userRepo.findByEmail(bidderEmail)
                .orElseThrow(() -> new RuntimeException("Bidder not found"));

        // Check that the new bid is higher than current highest
        List<Bids> existing = bidsRepo.findByAuctionOrderByAmountDesc(auction);
        double highest = existing.isEmpty()
                ? auction.getStartingPrice()
                : existing.get(0).getAmount();

        if (bidReq.getAmount() <= highest) {
            throw new RuntimeException("Bid must be greater than current highest (" + highest + ")");
        }

        // Save new bid
        Bids newBid = Bids.builder()
                .auction(auction)
                .bidder(bidder)
                .amount(bidReq.getAmount())
                .build();
        bidsRepo.save(newBid);

        // Return the updated auction detail (you’ll map bids → response in your mapper)
        // fetch the updated bid list
        List<BidResponse> updatedBids = bidsRepo
                .findByAuctionOrderByAmountDesc(auction)
                .stream()
                .map(this::toBidDto)
                .collect(Collectors.toList());

        return toDetailDto(auction, updatedBids);

    }

    @Override
    public List<BidResponse> findMyBids(String bidderEmail) {
        return bidsRepo.findByBidderEmailOrderByTimestampDesc(bidderEmail).stream()
                .map(this::toBidDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BidResponse> findMyWonBids(String bidderEmail) {
        // find all closed auctions where this user is winner
        return auctionRepo.findByWinnerEmail(bidderEmail).stream()
                // each Auction has exactly one “winning” bid: the highest
                .map(auction -> bidsRepo.findByAuctionOrderByAmountDesc(auction).stream().findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::toBidDto)
                .collect(Collectors.toList());
    }



    // --- DTO mappers ---

    private AuctionListResponse toListDto(Auction a) {
        double currentMax = bidsRepo.findByAuction(a).stream()
                .map(Bids::getAmount)
                .max(Comparator.naturalOrder())
                .orElse(a.getStartingPrice());
        return new AuctionListResponse(
                a.getId(),
                a.getCar().getId(),
                a.getCar().getMake(),
                a.getCar().getModel(),
                currentMax,
                a.getEndTime(),
                a.getIsClosed()
        );
    }

    private BidResponse toBidDto(Bids b) {
        return new BidResponse(
                b.getId(),
                b.getBidder().getEmail(),
                b.getAmount(),
                b.getTimestamp()
        );
    }

    private AuctionDetailResponse toDetailDto(Auction a, List<BidResponse> bids) {
        double currentMax = bids.stream()
                .map(BidResponse::getAmount)
                .max(Comparator.naturalOrder())
                .orElse(a.getStartingPrice());
        return new AuctionDetailResponse(
                a.getId(),
                a.getCar().getId(),
                a.getCar().getMake(),
                a.getCar().getModel(),
                a.getStartingPrice(),
                currentMax,
                a.getStartTime(),
                a.getEndTime(),
                a.getIsClosed(),
                a.getCar().getOwner().getEmail(),
                bids
        );
    }
}
