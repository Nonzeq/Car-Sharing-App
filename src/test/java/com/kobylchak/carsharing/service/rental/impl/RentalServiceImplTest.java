package com.kobylchak.carsharing.service.rental.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.kobylchak.carsharing.dto.car.CarDto;
import com.kobylchak.carsharing.dto.rental.CreateRentalRequestDto;
import com.kobylchak.carsharing.dto.rental.RentalDto;
import com.kobylchak.carsharing.dto.rental.RentalSearchParameters;
import com.kobylchak.carsharing.exception.RentalProcessingException;
import com.kobylchak.carsharing.mapper.rental.RentalMapper;
import com.kobylchak.carsharing.model.Car;
import com.kobylchak.carsharing.model.Rental;
import com.kobylchak.carsharing.model.Role;
import com.kobylchak.carsharing.model.User;
import com.kobylchak.carsharing.model.enums.UserRole;
import com.kobylchak.carsharing.repository.car.CarRepository;
import com.kobylchak.carsharing.repository.rental.RentalRepository;
import com.kobylchak.carsharing.repository.rental.RentalSpecificationBuilder;
import com.kobylchak.carsharing.service.notification.NotificationService;
import com.kobylchak.carsharing.service.notification.message.impl.TelegramMessageBuilder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class RentalServiceImplTest {
    @InjectMocks
    private RentalServiceImpl rentalService;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalSpecificationBuilder rentalSpecificationBuilder;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private CarRepository carRepository;
    @Mock
    private NotificationService telegramNotificationService;
    @Mock
    private Specification<Rental> specification;
    
    @Test
    public void createRental_ValidParameters_ShouldReturnRentalDto() {
        final Long carId = 1L;
        final int initialInventory = 5;
        final int expectedInventory = 4;
        LocalDate returnDate = getReturnDate();
        CreateRentalRequestDto createRentalRequestDto = new CreateRentalRequestDto();
        createRentalRequestDto.setCarId(carId);
        createRentalRequestDto.setReturnDate(returnDate);
        Car car = getCar();
        car.setInventory(initialInventory);
        User user = getUser();
        Rental rental = getRental();
        RentalDto rentalDto = getRentalDto();
        
        when(carRepository.findById(createRentalRequestDto.getCarId()))
                .thenReturn(Optional.of(car));
        when(rentalRepository.findAllByUserIdAndIsActive(user.getId())).thenReturn(List.of());
        when(rentalMapper.toModel(createRentalRequestDto)).thenReturn(rental);
        when(carRepository.save(car)).thenReturn(car);
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(telegramNotificationService.messageBuilder()).thenReturn(new TelegramMessageBuilder());
        when(rentalMapper.toDto(rental)).thenReturn(rentalDto);
        
        RentalDto actual = rentalService.createRental(createRentalRequestDto, user);
        
        assertNotNull(actual);
        assertEquals(rentalDto, actual);
        assertEquals(expectedInventory, car.getInventory());
        verify(carRepository, times(1)).save(car);
        verify(carRepository, times(1)).findById(carId);
        verify(rentalRepository, times(1)).save(rental);
        verify(rentalRepository, times(1))
                .findAllByUserIdAndIsActive(user.getId());
        verifyNoMoreInteractions(carRepository, rentalRepository);
    }
    
    @Test
    public void createRental_InvalidParametersCarId_ShouldThrowRentalProcessingException() {
        Long carId = 1L;
        LocalDate returnDate = getReturnDate();
        CreateRentalRequestDto createRentalRequestDto = new CreateRentalRequestDto();
        createRentalRequestDto.setCarId(carId);
        createRentalRequestDto.setReturnDate(returnDate);
        
        when(carRepository.findById(carId)).thenReturn(Optional.empty());
        
        String expected = "Car not found";
        RentalProcessingException rentalProcessingException = assertThrows(
                RentalProcessingException.class,
                () -> rentalService.createRental(
                        createRentalRequestDto,
                        getUser()));
        assertNotNull(rentalProcessingException);
        assertEquals(expected, rentalProcessingException.getMessage());
        
    }
    
    @Test
    public void createRental_UserHaveActiveRental_ShouldThrowRentalProcessingException() {
        Long carId = 1L;
        LocalDate returnDate = getReturnDate();
        CreateRentalRequestDto createRentalRequestDto = new CreateRentalRequestDto();
        createRentalRequestDto.setCarId(carId);
        createRentalRequestDto.setReturnDate(returnDate);
        
        when(carRepository.findById(carId)).thenReturn(Optional.of(new Car()));
        when(rentalRepository.findAllByUserIdAndIsActive(1L)).thenReturn(List.of(new Rental()));
        String expected = "You already have active rental";
        RentalProcessingException rentalProcessingException = assertThrows(
                RentalProcessingException.class,
                () -> rentalService.createRental(
                        createRentalRequestDto,
                        getUser()));
        assertNotNull(rentalProcessingException);
        assertEquals(expected, rentalProcessingException.getMessage());
        
    }
    
    @Test
    public void createRental_NoAvailableCars_ShouldThrowRentalProcessingException() {
        Long carId = 1L;
        Car car = getCar();
        car.setInventory(0);
        LocalDate returnDate = getReturnDate();
        CreateRentalRequestDto createRentalRequestDto = new CreateRentalRequestDto();
        createRentalRequestDto.setCarId(carId);
        createRentalRequestDto.setReturnDate(returnDate);
        
        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(rentalRepository.findAllByUserIdAndIsActive(1L)).thenReturn(List.of());
        String expected = "No free cars";
        RentalProcessingException rentalProcessingException = assertThrows(
                RentalProcessingException.class,
                () -> rentalService.createRental(
                        createRentalRequestDto,
                        getUser()));
        assertNotNull(rentalProcessingException);
        assertEquals(expected, rentalProcessingException.getMessage());
        
    }
    
    @Test
    public void getRentalsByParameters_ForCustomerAndManager_ShouldReturnListRentalDtos() {
        final RentalSearchParameters rentalSearchParameters = new RentalSearchParameters();
        Role managerRole = new Role();
        managerRole.setName(UserRole.MANAGER);
        Role customerRole = new Role();
        customerRole.setName(UserRole.CUSTOMER);
        
        User manager = getUser();
        manager.setRole(managerRole);
        
        User customer = getUser();
        customer.setRole(customerRole);
        
        rentalSearchParameters.setUserId("1");
        rentalSearchParameters.setIsActive("true");
        
        List<Rental> rentals = List.of(new Rental());
        List<RentalDto> rentalsDto = List.of(new RentalDto());
        
        when(rentalSpecificationBuilder.build(rentalSearchParameters)).thenReturn(specification);
        when(rentalRepository.findAll(specification)).thenReturn(rentals);
        when(rentalMapper.toDtos(rentals)).thenReturn(rentalsDto);
        
        List<RentalDto> actualForManager = rentalService.getRentalsByParameters(
                rentalSearchParameters, manager);
        List<RentalDto> actualForCustomer = rentalService.getRentalsByParameters(
                rentalSearchParameters, customer);
        assertFalse(actualForManager.isEmpty());
        assertEquals(1, actualForManager.size());
        assertFalse(actualForCustomer.isEmpty());
        assertEquals(1, actualForCustomer.size());
    }
    
    @Test
    public void getRentalById_ValidRentalId_ShouldReturnRentalDto() {
        User user = getUser();
        Long rentalId = 1L;
        Rental rental = getRental();
        RentalDto rentalDto = getRentalDto();
        rentalDto.setId(rentalId);
        rental.setId(rentalId);
        when(rentalRepository.findByUserAndId(user, rentalId)).thenReturn(Optional.of(rental));
        when(rentalMapper.toDto(rental)).thenReturn(rentalDto);
        
        RentalDto actual = rentalService.getRentalById(rentalId, user);
        
        assertNotNull(actual);
        assertEquals(rentalDto.getId(), actual.getId());
        assertEquals(rentalDto, actual);
    }
    
    @Test
    public void getRentalById_InvalidRentalId_ShouldThrowRentalProcessingException() {
        User user = getUser();
        Long rentalId = 1L;
        Rental rental = getRental();
        RentalDto rentalDto = getRentalDto();
        rentalDto.setId(rentalId);
        rental.setId(rentalId);
        when(rentalRepository.findByUserAndId(user, rentalId)).thenReturn(Optional.empty());
        
        RentalProcessingException rentalProcessingException = assertThrows(
                RentalProcessingException.class,
                () -> rentalService.getRentalById(rentalId, user));
        String actualMessage = "Rental with id: " + rentalId + " not found";
        
        assertNotNull(rentalProcessingException);
        assertEquals(actualMessage, rentalProcessingException.getMessage());
    }
    
    @Test
    public void returnRental_ValidParametersAndIsNotActive_ShouldReturnRentalDto() {
        final int initialInventory = 4;
        final int expectedInventory = 5;
        final User user = getUser();
        final Long rentalId = 1L;
        final Long carId = 1L;
        Rental rental = getRental();
        rental.setId(rentalId);
        rental.setActive(true);
        RentalDto rentalDto = getRentalDto();
        rentalDto.setId(rentalId);
        Car car = getCar();
        car.setId(carId);
        car.setInventory(initialInventory);
        
        when(rentalRepository.findByUserAndId(user, rentalId)).thenReturn(Optional.of(rental));
        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(carRepository.save(car)).thenReturn(car);
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(rentalMapper.toDto(rental)).thenReturn(rentalDto);
        
        RentalDto actual = rentalService.returnRental(rentalId, user);
        
        assertNotNull(actual);
        assertEquals(expectedInventory, car.getInventory());
        assertFalse(rental.isActive());
        assertEquals(rentalDto, actual);
    }
    
    @Test
    public void returnRental_InvalidRentalId_ShouldThrowRentalProcessingException() {
        User user = getUser();
        Long rentalId = 1L;
        
        when(rentalRepository.findByUserAndId(user, rentalId)).thenReturn(Optional.empty());
        
        RentalProcessingException rentalProcessingException = assertThrows(
                RentalProcessingException.class,
                () -> rentalService.returnRental(rentalId, user));
        String expectedMessage = "Rental with id: " + rentalId + " not found";
        
        assertNotNull(rentalProcessingException);
        assertEquals(expectedMessage, rentalProcessingException.getMessage());
    }
    
    @Test
    public void returnRental_InvalidCarId_ShouldThrowRentalProcessingException() {
        User user = getUser();
        Long rentalId = 1L;
        Long carId = 1L;
        
        Rental rental = getRental();
        rental.getCar().setId(carId);
        rental.setActive(true);
        when(rentalRepository.findByUserAndId(user, rentalId))
                .thenReturn(Optional.of(rental));
        
        RentalProcessingException rentalProcessingException = assertThrows(
                RentalProcessingException.class,
                () -> rentalService.returnRental(rentalId, user));
        String expectedMessage = "Car with id: " + carId + " not found";
        
        assertNotNull(rentalProcessingException);
        assertEquals(expectedMessage, rentalProcessingException.getMessage());
    }
    
    @Test
    public void returnRental_UserAlreadyReturnRental_ShouldThrowRentalProcessingException() {
        User user = getUser();
        Long rentalId = 1L;
        Long carId = 1L;
        
        Rental rental = getRental();
        rental.getCar().setId(carId);
        rental.setActive(false);
        when(rentalRepository.findByUserAndId(user, rentalId))
                .thenReturn(Optional.of(rental));
        
        RentalProcessingException rentalProcessingException = assertThrows(
                RentalProcessingException.class,
                () -> rentalService.returnRental(rentalId, user));
        String expectedMessage = "Rental with id: " + rentalId + " is already closed";
        
        assertNotNull(rentalProcessingException);
        assertEquals(expectedMessage, rentalProcessingException.getMessage());
    }
    
    private Car getCar() {
        Car car = new Car();
        car.setId(1L);
        car.setDailyFee(BigDecimal.valueOf(30));
        car.setInventory(5);
        car.setBrand("TOYOTA");
        car.setModel("S10");
        return car;
    }
    
    private CarDto getCarDto() {
        Car car = getCar();
        CarDto carDto = new CarDto();
        carDto.setId(car.getId());
        carDto.setBrand(car.getBrand());
        carDto.setModel(car.getModel());
        carDto.setInventory(car.getInventory());
        carDto.setDailyFee(car.getDailyFee());
        return carDto;
    }
    
    private User getUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setFirstName("test");
        user.setLastName("test");
        return user;
    }
    
    private Rental getRental() {
        LocalDate returnDate = getReturnDate();
        User user = getUser();
        Car car = getCar();
        Rental rental = new Rental();
        rental.setRentalDate(returnDate.minusDays(10));
        rental.setRentalDate(returnDate);
        rental.setCar(car);
        rental.setUser(user);
        return rental;
    }
    
    private RentalDto getRentalDto() {
        Rental rental = getRental();
        CarDto carDto = getCarDto();
        RentalDto rentalDto = new RentalDto();
        rentalDto.setRentalDate(rental.getRentalDate());
        rentalDto.setId(rental.getId());
        rentalDto.setCar(carDto);
        rentalDto.setReturnDate(rental.getReturnDate());
        return rentalDto;
    }
    
    private LocalDate getReturnDate() {
        return LocalDate.now().minusDays(5);
    }
    
}
