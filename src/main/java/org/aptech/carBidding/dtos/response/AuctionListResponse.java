package org.aptech.carBidding.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuctionListResponse {
    private Long id;
    private Long carId;
    private String carMake;
    private String carModel;
    private Double currentHighestBid;
    private LocalDateTime endTime;
    private Boolean isClosed;
}
