package org.aptech.carBidding.controllers;

import lombok.RequiredArgsConstructor;
import org.aptech.carBidding.dtos.requests.BidRequest;
import org.aptech.carBidding.dtos.requests.CreateAuctionRequest;
import org.aptech.carBidding.dtos.response.AuctionDetailResponse;
import org.aptech.carBidding.dtos.response.AuctionListResponse;
import org.aptech.carBidding.services.AuctionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
@Validated
public class AuctionController {

    private final AuctionService auctionService;

    /** Anyone logged in can view active auctions */
    @GetMapping
    public List<AuctionListResponse> listAll() {
        return auctionService.listAuctions();
    }

    /** Anyone logged in can view one auction’s details */
    @GetMapping("/{id}")
    public AuctionDetailResponse getOne(@PathVariable Long id) {
        return auctionService.getAuctionById(id);
    }

    /** Only SELLERs can create new auctions */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('SELLER')")
    public AuctionDetailResponse createAuction(
            @RequestBody @Validated CreateAuctionRequest req,
            @AuthenticationPrincipal UserDetails principal
    ) {
        return auctionService.createAuction(req, principal.getUsername());
    }

    /** Only the SELLER who owns the car (or ADMIN) can update */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public AuctionDetailResponse updateAuction(
            @PathVariable Long id,
            @RequestBody @Validated CreateAuctionRequest req,
            @AuthenticationPrincipal UserDetails principal
    ) {
        return auctionService.updateAuction(id, req, principal.getUsername());
    }

    /** Only the SELLER who owns the auction (or ADMIN) can delete */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public void deleteAuction(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal
    ) {
        auctionService.deleteAuction(id, principal.getUsername());
    }

    /** Only BIDDERs can place bids */
    @PostMapping("/{auctionId}/bids")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('BIDDER')")
    public AuctionDetailResponse placeBid(
            @PathVariable Long auctionId,
            @RequestBody @Validated BidRequest bidReq,
            @AuthenticationPrincipal UserDetails principal
    ) {
        // returns the updated auction with the new bid history
        return auctionService.placeBid(auctionId, bidReq, principal.getUsername());
    }
}
