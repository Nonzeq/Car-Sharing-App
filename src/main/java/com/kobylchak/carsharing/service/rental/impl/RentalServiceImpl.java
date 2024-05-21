package com.kobylchak.carsharing.service.rental.impl;

import com.kobylchak.carsharing.dto.rental.CreateRentalRequestDto;
import com.kobylchak.carsharing.dto.rental.RentalDto;
import com.kobylchak.carsharing.dto.rental.RentalSearchParameters;
import com.kobylchak.carsharing.exception.RentalProcessingException;
import com.kobylchak.carsharing.mapper.RentalMapper;
import com.kobylchak.carsharing.model.Car;
import com.kobylchak.carsharing.model.Rental;
import com.kobylchak.carsharing.model.User;
import com.kobylchak.carsharing.model.enums.UserRole;
import com.kobylchak.carsharing.repository.car.CarRepository;
import com.kobylchak.carsharing.repository.rental.RentalRepository;
import com.kobylchak.carsharing.repository.rental.RentalSpecificationBuilder;
import com.kobylchak.carsharing.service.notification.NotificationService;
import com.kobylchak.carsharing.service.notification.message.MessageBuilder;
import com.kobylchak.carsharing.service.rental.RentalService;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalSpecificationBuilder rentalSpecificationBuilder;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final NotificationService telegramNotificationService;
    
    @Override
    public RentalDto createRental(CreateRentalRequestDto requestDto, User user) {
        Car car = carRepository.findById(requestDto.getCarId())
                          .orElseThrow(() -> new RentalProcessingException("Car not found"));
        List<Rental> rentals = rentalRepository.findAllByUserIdAndIsActive(user.getId());
        if (!rentals.isEmpty()) {
            throw new RentalProcessingException("You already have active rental");
        }
        if (car.getInventory() > 0) {
            Rental rental = rentalMapper.toModel(requestDto);
            rental.setUser(user);
            rental.setCar(car);
            car.setInventory(car.getInventory() - 1);
            carRepository.save(car);
            Rental newRental = rentalRepository.save(rental);
            RentalDto newRentalDto = rentalMapper.toDto(newRental);
            telegramNotificationService.sendNotification(createMessageForNewRental(rental));
            return newRentalDto;
        }
        throw new RentalProcessingException("No free cars");
    }
    
    @Override
    public List<RentalDto> getRentalsByParameters(RentalSearchParameters parameters, User user) {
        if (user.getRole().getName().equals(UserRole.MANAGER)) {
            Specification<Rental> specification = rentalSpecificationBuilder.build(parameters);
            List<Rental> rentals = rentalRepository.findAll(specification);
            return rentalMapper.toDtos(rentals);
        }
        parameters.setUserId(user.getId().toString());
        Specification<Rental> specification = rentalSpecificationBuilder.build(parameters);
        List<Rental> rentals = rentalRepository.findAll(specification);
        return rentalMapper.toDtos(rentals);
    }
    
    @Override
    public RentalDto getRentalById(Long id, User user) {
        return rentalMapper.toDto(rentalRepository.findByUserAndId(user, id).orElseThrow(
                () -> new RentalProcessingException("Rental with id: " + id + " not found")));
    }
    
    @Override
    public RentalDto returnRental(Long id, User user) {
        Rental rental = rentalRepository.findByUserAndId(user, id).orElseThrow(
                () -> new RentalProcessingException("Rental with id: " + id + " not found"));
        if (rental.isActive()) {
            rental.setActualReturnDate(LocalDate.now());
            rental.setActive(false);
            Car car = carRepository.findById(rental.getCar().getId()).orElseThrow(
                    () -> new RentalProcessingException("Car with id: "
                                                        + id
                                                        + " not found"));
            car.setInventory(car.getInventory() + 1);
            carRepository.save(car);
            return rentalMapper.toDto(rentalRepository.save(rental));
        }
        throw new RentalProcessingException("Rental with id: " + id + " is already closed");
    }
    
    @Scheduled(cron = "0 0 15 * * *")
    public void checkOverdueRentals() {
        List<Rental> allOverdueRentals = rentalRepository.findAllOverdueRentals();
        if (allOverdueRentals.isEmpty()) {
            String message = telegramNotificationService.messageBuilder()
                                     .title("No rentals overdue today! " + LocalDate.now())
                                     .build();
            telegramNotificationService.sendNotification(message);
        } else {
            MessageBuilder builder = telegramNotificationService.messageBuilder()
                                             .title("Overdue rentals for date: " + LocalDate.now());
            for (Rental rental : allOverdueRentals) {
                long daysOfOverdue = ChronoUnit.DAYS.between(rental.getReturnDate(),
                                                             LocalDate.now());
                builder.listItems().item("ID: " + rental.getId())
                        .item("Rental date: " + rental.getRentalDate())
                        .item("Car brand: " + rental.getCar().getBrand())
                        .item("Car model: " + rental.getCar().getModel())
                        .item("Return date: " + rental.getReturnDate())
                        .item("Actual return date: " + rental.getActualReturnDate())
                        .item("Days of overdue: " + daysOfOverdue)
                        .item("User email : " + rental.getUser().getEmail()).item("\n")
                        .buildItemsList();
            }
            telegramNotificationService.sendNotification(builder.build());
        }
    }
    
    private String createMessageForNewRental(Rental rental) {
        return telegramNotificationService.messageBuilder()
                       .title("Create new Rental").listItems()
                       .item("ID: " + rental.getId()).item("Rental date: " + rental.getRentalDate())
                       .item("Car brand: " + rental.getCar().getBrand())
                       .item("Car model: " + rental.getCar().getModel())
                       .item("Return date: " + rental.getReturnDate())
                       .item("Actual return date: " + rental.getActualReturnDate())
                       .item("User email : " + rental.getUser().getEmail()).buildItemsList()
                       .build();
    }
}
