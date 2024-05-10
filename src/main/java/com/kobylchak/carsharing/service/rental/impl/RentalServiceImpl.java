package com.kobylchak.carsharing.service.rental.impl;

import com.kobylchak.carsharing.dto.rental.CreateRentalRequestDto;
import com.kobylchak.carsharing.dto.rental.RentalDto;
import com.kobylchak.carsharing.dto.rental.RentalSearchParameters;
import com.kobylchak.carsharing.exception.RentalProcessingException;
import com.kobylchak.carsharing.mapper.rental.RentalMapper;
import com.kobylchak.carsharing.model.Car;
import com.kobylchak.carsharing.model.Rental;
import com.kobylchak.carsharing.model.User;
import com.kobylchak.carsharing.model.enums.UserRole;
import com.kobylchak.carsharing.repository.car.CarRepository;
import com.kobylchak.carsharing.repository.rental.RentalRepository;
import com.kobylchak.carsharing.repository.rental.RentalSpecificationBuilder;
import com.kobylchak.carsharing.service.notification.NotificationService;
import com.kobylchak.carsharing.service.rental.RentalMessageGenerator;
import com.kobylchak.carsharing.service.rental.RentalService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalSpecificationBuilder rentalSpecificationBuilder;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final NotificationService telegramNotificationService;
    private final RentalMessageGenerator rentalMessageGenerator;
    
    @Override
    @Transactional
    public RentalDto createRental(CreateRentalRequestDto requestDto, User user) {
        Car car = carRepository.findById(requestDto.getCarId())
                               .orElseThrow(() -> new RentalProcessingException("Car not found"));
        if (car.getInventory() > 0) {
            Rental rental = rentalMapper.toModel(requestDto);
            rental.setUser(user);
            rental.setCar(car);
            car.setInventory(car.getInventory() - 1);
            carRepository.save(car);
            Rental newRental = rentalRepository.save(rental);
            RentalDto newRentalDto = rentalMapper.toDto(newRental);
            telegramNotificationService.sendNotification(
                    rentalMessageGenerator.getForCreating(newRental));
            return newRentalDto;
        }
        throw new RentalProcessingException("No free cars");
    }
    
    @Override
    public List<RentalDto> getRentalsByParameters(RentalSearchParameters parameters, User user) {
        if (user.getRole()
                .getName()
                .equals(UserRole.MANAGER)) {
            Specification<Rental> specification = rentalSpecificationBuilder.build(parameters);
            List<Rental> rentals = rentalRepository.findAll(specification);
            return rentalMapper.toDtos(rentals);
        }
        parameters.setUserId(user.getId()
                                 .toString());
        Specification<Rental> specification = rentalSpecificationBuilder.build(parameters);
        List<Rental> rentals = rentalRepository.findAll(specification);
        return rentalMapper.toDtos(rentals);
    }
    
    @Override
    public RentalDto getRentalById(Long id, User user) {
        return rentalMapper.toDto(rentalRepository.findByUserAndId(user, id)
                                                  .orElseThrow(() -> new RentalProcessingException(
                                                          "Rental with id: "
                                                          + id
                                                          + " not found")));
    }
    
    @Override
    @Transactional
    public RentalDto returnRental(Long id, User user) {
        Rental rental = rentalRepository.findByUserAndId(user, id)
                                        .orElseThrow(
                                                () -> new RentalProcessingException(
                                                        "Rental with id: "
                                                        + id
                                                        + " not found"));
        if (rental.isActive()) {
            rental.setActualReturnDate(LocalDate.now());
            rental.setActive(false);
            Car car = carRepository.findById(rental.getCar()
                                                   .getId())
                                   .orElseThrow(() -> new RentalProcessingException(
                                           "Car with id: "
                                           + id
                                           + " not "
                                           + "found"));
            car.setInventory(car.getInventory() + 1);
            carRepository.save(car);
            return rentalMapper.toDto(rentalRepository.save(rental));
        }
        throw new RentalProcessingException("Rental with id: " + id + " is already closed");
    }
    
    @Scheduled(cron = "0 1 1 * * *")
    public void checkOverdueRentals() {
        List<Rental> allOverdueRentals = rentalRepository.findAllOverdueRentals();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(rentalMessageGenerator.getTitleForOverdue());
        if (allOverdueRentals.isEmpty()) {
            stringBuilder.append(rentalMessageGenerator.getForNotOverdue());
            telegramNotificationService.sendNotification(stringBuilder.toString());
        } else {
            for (Rental rental : allOverdueRentals) {
                stringBuilder.append(rentalMessageGenerator.getForOverdue(rental))
                             .append("\n");
            }
            telegramNotificationService.sendNotification(stringBuilder.toString());
        }
    }
}
