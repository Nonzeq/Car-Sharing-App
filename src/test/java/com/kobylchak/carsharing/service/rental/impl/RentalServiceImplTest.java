package com.kobylchak.carsharing.service.rental.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.kobylchak.carsharing.mapper.rental.RentalMapper;
import com.kobylchak.carsharing.repository.car.CarRepository;
import com.kobylchak.carsharing.repository.rental.RentalRepository;
import com.kobylchak.carsharing.repository.rental.RentalSpecificationBuilder;
import com.kobylchak.carsharing.service.notification.NotificationService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RentalServiceImplTest {
    @InjectMocks
    private RentalServiceImpl rentalService;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalSpecificationBuilder rentalSpecificationBuilder;
    @Mock
    private  RentalMapper rentalMapper;
    @Mock
    private  CarRepository carRepository;
    @Mock
    private  NotificationService telegramNotificationService;
    
    

}