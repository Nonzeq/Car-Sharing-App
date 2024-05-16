package com.kobylchak.carsharing.service.car.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.kobylchak.carsharing.dto.car.CarDto;
import com.kobylchak.carsharing.dto.car.CreateCarRequestDto;
import com.kobylchak.carsharing.mapper.car.CarMapper;
import com.kobylchak.carsharing.model.Car;
import com.kobylchak.carsharing.model.enums.CarType;
import com.kobylchak.carsharing.repository.car.CarRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {
    @InjectMocks
    private CarServiceImpl carService;
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;
    
    @Test
    public void createCar_ValidCreateCarRequestDto_ShouldReturnsCarDto() {
        // Given
        CreateCarRequestDto requestDto = new CreateCarRequestDto();
        requestDto.setModel("Model");
        requestDto.setBrand("Brand");
        requestDto.setType(CarType.SEDAN.name());
        requestDto.setInventory(5);
        requestDto.setDailyFee(BigDecimal.valueOf(40));
        
        Car car = new Car();
        car.setModel(requestDto.getModel());
        car.setBrand(requestDto.getBrand());
        car.setType(CarType.valueOf(requestDto.getType()));
        car.setInventory(requestDto.getInventory());
        car.setDailyFee(requestDto.getDailyFee());
        
        CarDto carDto = getCarDtoFRomCar(car);
        carDto.setId(1L);
        
        when(carMapper.toModel(requestDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(carDto);
        
        CarDto savedCarDto = carService.createCar(requestDto);
        
        assertThat(savedCarDto).isEqualTo(carDto);
        
        verify(carRepository, times(1)).save(car);
        verifyNoMoreInteractions(carRepository, carMapper);
    }
    
    @Test
    public void getCarById_ValidCarId_ShouldReturnsCarDto() {
        Long carId = 1L;
        Car car = getDefaultCar();
        car.setId(carId);
        
        CarDto carDto = getCarDtoFRomCar(car);
        carDto.setId(carId);
        
        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(carMapper.toDto(car)).thenReturn(carDto);
        
        CarDto carDtoById = carService.getCarById(carId);
        
        assertThat(carDtoById).isEqualTo(carDto);
        
        verify(carRepository, times(1)).findById(carId);
        verify(carMapper, times(1)).toDto(car);
        verifyNoMoreInteractions(carRepository, carMapper);
    }
    
    @Test
    public void getCarById_InvalidCarId_ShouldThrowEntityNotFoundException() {
        Long carId = 1L;
        
        when(carRepository.findById(carId)).thenReturn(Optional.empty());
        
        Exception exception = assertThrows(EntityNotFoundException.class,
                                           () -> carService.getCarById(carId));
        
        String expected = "Car with id:  " + carId + ", not found";
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        
        verify(carRepository, times(1)).findById(carId);
        verifyNoMoreInteractions(carRepository);
    }
    
    @Test
    public void updateCar_InvalidCarId_ShouldThrowEntityNotFoundException() {
        Long carId = 1L;
        
        when(carRepository.existsById(carId)).thenReturn(false);
        
        Exception exception = assertThrows(EntityNotFoundException.class,
                                           () -> carService.updateCar(carId,
                                                                      new CreateCarRequestDto()));
        String expected = "Car with id:  " + carId + ", not found";
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        
        verify(carRepository, times(1)).existsById(carId);
        verifyNoMoreInteractions(carRepository);
    }
    
    @Test
    public void getAllCars_ShouldReturnListCarDtos() {
        List<Car> cars = List.of(new Car(), new Car());
        List<CarDto> carDtos = List.of(new CarDto(), new CarDto());
        
        when(carRepository.findAll()).thenReturn(cars);
        when(carMapper.toDtos(cars)).thenReturn(carDtos);
        
        List<CarDto> allCars = carService.getAllCars();
        
        assertNotNull(allCars);
        assertEquals(cars.size(), allCars.size());
        verify(carRepository, times(1)).findAll();
        verifyNoMoreInteractions(carRepository);
        
    }
    
    private Car getDefaultCar() {
        Car car = new Car();
        car.setModel("Model");
        car.setBrand("Brand");
        car.setType(CarType.SEDAN);
        car.setInventory(10);
        car.setDailyFee(BigDecimal.valueOf(25));
        return car;
    }
    
    private CarDto getCarDtoFRomCar(Car car) {
        CarDto carDto = new CarDto();
        carDto.setModel(car.getModel());
        carDto.setBrand(car.getBrand());
        carDto.setType(String.valueOf(car.getType()));
        carDto.setInventory(car.getInventory());
        carDto.setDailyFee(car.getDailyFee());
        return carDto;
    }
    
}
