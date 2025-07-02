package org.aptech.carBidding.services;

import org.aptech.carBidding.dtos.requests.BidRequest;
import org.aptech.carBidding.dtos.requests.CreateAuctionRequest;
import org.aptech.carBidding.dtos.response.AuctionDetailResponse;
import org.aptech.carBidding.dtos.response.AuctionListResponse;

import java.util.List;

public interface AuctionService {
    List<AuctionListResponse> listAuctions();
    AuctionDetailResponse getAuctionById(Long id);
    AuctionDetailResponse createAuction(CreateAuctionRequest request, String sellerEmail);
    AuctionDetailResponse updateAuction(Long id, CreateAuctionRequest request, String sellerEmail);
    void deleteAuction(Long id, String sellerEmail);
    AuctionDetailResponse placeBid(Long auctionId, BidRequest bidReq, String bidderEmail);

}
