package org.aptech.carBidding.dtos.requests;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Partial update: all fields nullable, but validate if present.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarUpdateRequest {

    @Size(min = 1, message = "Make cannot be blank")
    private String make;

    @Size(min = 1, message = "Model cannot be blank")
    private String model;

    @PositiveOrZero(message = "Year must be zero or positive")
    private Integer year;

    private String description;
}
