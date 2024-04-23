package com.kobylchak.carsharing.service.car;

import com.kobylchak.carsharing.dto.car.CarDto;
import com.kobylchak.carsharing.dto.car.CreateCarRequestDto;
import com.kobylchak.carsharing.mapper.car.CarMapper;
import com.kobylchak.carsharing.model.Car;
import com.kobylchak.carsharing.repository.car.CarRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;
    
    @Override
    public CarDto createCar(CreateCarRequestDto requestDto) {
        Car car = carMapper.toModel(requestDto);
        return carMapper.toDto(carRepository.save(car));
    }
    
    @Override
    public List<CarDto> getAllCars() {
        return carMapper.toDtos(carRepository.findAll());
    }
    
    @Override
    public CarDto getCarById(Long id) {
        return carMapper.toDto(carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Car with id:  " + id + ", not found")));
    }
    
    @Override
    public CarDto updateCar(Long id, CreateCarRequestDto requestDto) {
        if (carRepository.existsById(id)) {
            Car car = carMapper.toModel(requestDto);
            car.setId(id);
            return carMapper.toDto(carRepository.save(car));
        }
        throw new EntityNotFoundException("Car with id:  " + id + ", not found");
    }
    
    @Override
    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }
}
