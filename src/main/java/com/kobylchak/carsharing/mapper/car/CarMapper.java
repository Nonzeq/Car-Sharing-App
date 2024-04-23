package com.kobylchak.carsharing.mapper.car;

import com.kobylchak.carsharing.config.MapperConfig;
import com.kobylchak.carsharing.dto.car.CarDto;
import com.kobylchak.carsharing.dto.car.CreateCarRequestDto;
import com.kobylchak.carsharing.model.Car;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    List<CarDto> toDtos(List<Car> cars);
    
    CarDto toDto(Car car);
    
    Car toModel(CreateCarRequestDto requestDto);
}
