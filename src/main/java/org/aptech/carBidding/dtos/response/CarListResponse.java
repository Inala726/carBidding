package org.aptech.carBidding.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarListResponse {
    private Long id;
    private String make;
    private String model;
    private Integer year;
    private Long ownerId;
}
