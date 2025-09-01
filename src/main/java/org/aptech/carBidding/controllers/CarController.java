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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    @PostMapping(consumes = {"multipart/form-data"})
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('SELLER')")
    public CarDetailResponse create(
            @RequestPart("request") @Validated CreateCarRequest req,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws IOException {
        return carService.createCar(req, image, userDetails.getUsername());
    }

    /** only SELLER (or ADMIN) can update cars */
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('SELLER')")
    public CarDetailResponse update(
            @PathVariable Long id,
            @RequestPart("request") @Validated CarUpdateRequest req,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        return carService.updateCar(id, req, image);
    }

    /** only SELLER (or ADMIN) can delete cars */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('SELLER')")
    public void delete(@PathVariable Long id) {
        carService.deleteCar(id);
    }
}