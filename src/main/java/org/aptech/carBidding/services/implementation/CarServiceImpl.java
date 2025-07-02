package org.aptech.carBidding.services.implementation;

import lombok.RequiredArgsConstructor;
import org.aptech.carBidding.dtos.requests.CarUpdateRequest;
import org.aptech.carBidding.dtos.requests.CreateCarRequest;
import org.aptech.carBidding.dtos.response.CarDetailResponse;
import org.aptech.carBidding.dtos.response.CarListResponse;
import org.aptech.carBidding.exceptions.ResourceNotFoundException;
import org.aptech.carBidding.models.Car;
import org.aptech.carBidding.models.User;
import org.aptech.carBidding.repository.CarRepository;
import org.aptech.carBidding.repository.UserRepository;
import org.aptech.carBidding.services.CarService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;

    @Override
    public List<CarListResponse> getAllCars() {
        return carRepository.findAll()
                .stream()
                .map(this::toListDto)
                .collect(Collectors.toList());
    }

    @Override
    public CarDetailResponse getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car", "id", id));
        return toDetailDto(car);
    }

    @Override
    public CarDetailResponse createCar(CreateCarRequest req, String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", ownerEmail));

        Car car = Car.builder()
                .make(req.getMake())
                .model(req.getModel())
                .year(req.getYear())
                .description(req.getDescription())
                .owner(owner)
                .build();

        Car saved = carRepository.save(car);
        return toDetailDto(saved);
    }

    @Override
    public CarDetailResponse updateCar(Long id, CarUpdateRequest req) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car", "id", id));

        if (req.getMake() != null) car.setMake(req.getMake());
        if (req.getModel() != null) car.setModel(req.getModel());
        if (req.getYear() != null) car.setYear(req.getYear());
        if (req.getDescription() != null) car.setDescription(req.getDescription());

        // flush/update happens on transaction commit
        return toDetailDto(car);
    }

    @Override
    public void deleteCar(Long id) {
        if (!carRepository.existsById(id)) {
            throw new ResourceNotFoundException("Car", "id", id);
        }
        carRepository.deleteById(id);
    }

    // --- mappers ---
    private CarListResponse toListDto(Car car) {
        return new CarListResponse(
                car.getId(),
                car.getMake(),
                car.getModel(),
                car.getYear(),
                car.getOwner().getId()
        );
    }

    private CarDetailResponse toDetailDto(Car car) {
        return new CarDetailResponse(
                car.getId(),
                car.getMake(),
                car.getModel(),
                car.getYear(),
                car.getDescription(),
                car.getOwner().getId(),
                car.getOwner().getEmail(),
                null,
                null
        );
    }
}
