package org.aptech.carBidding.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAuctionRequest {
    private Long carId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double startingPrice;
}
