package com.kobylchak.carsharing.service.car;

import com.kobylchak.carsharing.dto.car.CarDto;
import com.kobylchak.carsharing.dto.car.CreateCarRequestDto;
import java.util.List;

public interface CarService {
    CarDto createCar(CreateCarRequestDto requestDto);
    
    List<CarDto> getAllCars();
    
    CarDto getCarById(Long id);
    
    CarDto updateCar(Long id, CreateCarRequestDto requestDto);
    
    void deleteCar(Long id);
}
