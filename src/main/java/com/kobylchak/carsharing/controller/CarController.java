package com.kobylchak.carsharing.controller;

import com.kobylchak.carsharing.dto.car.CarDto;
import com.kobylchak.carsharing.dto.car.CreateCarRequestDto;
import com.kobylchak.carsharing.service.car.CarService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;
    
    @GetMapping
    public List<CarDto> getAllCars() {
        return carService.getAllCars();
    }
    
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public CarDto createCar(@RequestBody @Valid CreateCarRequestDto requestDto) {
        return carService.createCar(requestDto);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    public CarDto getCar(@PathVariable Long id) {
        return carService.getCarById(id);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public CarDto updateCar(@PathVariable Long id,
                            @RequestBody @Valid CreateCarRequestDto requestDto) {
        return carService.updateCar(id, requestDto);
    }
    
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }
}
