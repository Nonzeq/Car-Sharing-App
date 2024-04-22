package com.kobylchak.carsharing.controller;

import com.kobylchak.carsharing.dto.car.CarDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/cars")
@RequiredArgsConstructor
public class CarController {

    @GetMapping
    public List<CarDto> getCars() {
        CarDto carDto = new CarDto();
        carDto.setBrand("AUDI");
        carDto.setModel("B12");
        return List.of(carDto);
    }
}
