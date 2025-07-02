package org.aptech.carBidding.services;

import org.aptech.carBidding.dtos.requests.CarUpdateRequest;
import org.aptech.carBidding.dtos.requests.CreateCarRequest;
import org.aptech.carBidding.dtos.response.CarDetailResponse;
import org.aptech.carBidding.dtos.response.CarListResponse;

import java.util.List;

public interface CarService {
    List<CarListResponse> getAllCars();
    CarDetailResponse getCarById(Long id);
    CarDetailResponse createCar(CreateCarRequest request, String ownerEmail);
    CarDetailResponse updateCar(Long id, CarUpdateRequest request);
    void deleteCar(Long id);
}
