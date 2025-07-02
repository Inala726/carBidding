package org.aptech.carBidding.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuctionDetailResponse {
    private Long id;
    private Long carId;
    private String carMake;
    private String carModel;
    private Double startingPrice;
    private Double currentHighestBid;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isClosed;
    private String sellerEmail;
    private List<BidResponse> bidHistory;
}
