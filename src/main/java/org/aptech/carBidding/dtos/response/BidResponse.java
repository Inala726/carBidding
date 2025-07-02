package org.aptech.carBidding.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidResponse {
    private Long id;
    private String bidderEmail;
    private Double amount;
    private LocalDateTime timestamp;
}
