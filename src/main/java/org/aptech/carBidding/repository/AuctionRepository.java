package org.aptech.carBidding.repository;

import org.aptech.carBidding.models.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    /** find only auctions that are still open */
    List<Auction> findByIsClosedFalse();
}
