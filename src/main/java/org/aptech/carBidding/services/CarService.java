package org.aptech.carBidding.services;

import org.aptech.carBidding.dtos.requests.CarUpdateRequest;
import org.aptech.carBidding.dtos.requests.CreateCarRequest;
import org.aptech.carBidding.dtos.response.CarDetailResponse;
import org.aptech.carBidding.dtos.response.CarListResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CarService {
    List<CarListResponse> getAllCars();
    CarDetailResponse getCarById(Long id);
    CarDetailResponse createCar(CreateCarRequest request, MultipartFile image, String ownerEmail) throws IOException;
    CarDetailResponse updateCar(Long id, CarUpdateRequest request, MultipartFile image) throws IOException;
    void deleteCar(Long id);
}