package org.aptech.carBidding.repository;

import org.aptech.carBidding.models.Bids;
import org.aptech.carBidding.models.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidsRepository extends JpaRepository<Bids, Long> {
    List<Bids> findByAuction(Auction auction);
    List<Bids> findByAuctionOrderByAmountDesc(Auction auction);
}
