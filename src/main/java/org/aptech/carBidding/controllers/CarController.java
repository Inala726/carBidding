package org.aptech.carBidding.controllers;

import lombok.RequiredArgsConstructor;
import org.aptech.carBidding.dtos.requests.CarUpdateRequest;
import org.aptech.carBidding.dtos.requests.CreateCarRequest;
import org.aptech.carBidding.dtos.response.CarDetailResponse;
import org.aptech.carBidding.dtos.response.CarListResponse;
import org.aptech.carBidding.services.CarService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
@Validated
public class CarController {

    private final CarService carService;

    /** anyone logged in can see the list of cars */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<CarListResponse> listAll() {
        return carService.getAllCars();
    }

    /** anyone logged in can view a single car */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public CarDetailResponse getOne(@PathVariable Long id) {
        return carService.getCarById(id);
    }

    /** only SELLER (or ADMIN) can create a car listing */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('SELLER')")
    public CarDetailResponse create(
            @RequestBody @Validated CreateCarRequest req,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return carService.createCar(req, userDetails.getUsername());
    }

    /** only SELLER (or ADMIN) can update cars */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public CarDetailResponse update(
            @PathVariable Long id,
            @RequestBody @Validated CarUpdateRequest req
    ) {
        return carService.updateCar(id, req);
    }

    /** only SELLER (or ADMIN) can delete cars */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('SELLER')")
    public void delete(@PathVariable Long id) {
        carService.deleteCar(id);
    }
}
