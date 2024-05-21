package com.kobylchak.carsharing.controller;

import com.kobylchak.carsharing.dto.car.CarDto;
import com.kobylchak.carsharing.dto.car.CreateCarRequestDto;
import com.kobylchak.carsharing.service.car.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/cars")
@RequiredArgsConstructor
@Tag(
        name = "Cars management",
        description = "Endpoints for cars management"
)
public class CarController {
    private final CarService carService;
    
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get list of all available cars")
    public List<CarDto> getAllCars() {
        return carService.getAllCars();
    }
    
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new car")
    public CarDto createCar(@RequestBody @Valid CreateCarRequestDto requestDto) {
        return carService.createCar(requestDto);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get car by id")
    public CarDto getCar(@PathVariable Long id) {
        return carService.getCarById(id);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update all car fields by id")
    public CarDto updateCar(@PathVariable Long id,
                            @RequestBody @Valid CreateCarRequestDto requestDto) {
        return carService.updateCar(id, requestDto);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete car by id")
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }
}
