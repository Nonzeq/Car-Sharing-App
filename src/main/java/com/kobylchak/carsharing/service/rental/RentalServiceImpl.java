package com.kobylchak.carsharing.service.rental;

import com.kobylchak.carsharing.dto.rental.CreateRentalRequestDto;
import com.kobylchak.carsharing.dto.rental.RentalDto;
import com.kobylchak.carsharing.dto.rental.RentalSearchParameters;
import com.kobylchak.carsharing.exception.RentalException;
import com.kobylchak.carsharing.mapper.rental.RentalMapper;
import com.kobylchak.carsharing.model.Car;
import com.kobylchak.carsharing.model.Rental;
import com.kobylchak.carsharing.model.User;
import com.kobylchak.carsharing.repository.car.CarRepository;
import com.kobylchak.carsharing.repository.rental.RentalRepository;
import com.kobylchak.carsharing.repository.rental.RentalSpecificationBuilder;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalSpecificationBuilder rentalSpecificationBuilder;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    
    @Override
    @Transactional
    public RentalDto createRental(CreateRentalRequestDto requestDto, User user) {
        Car car = carRepository.findById(requestDto.getCarId())
                               .orElseThrow(() -> new RentalException("Car not found"));
        if (car.getInventory() > 0) {
            Rental rental = rentalMapper.toModel(requestDto);
            rental.setUser(user);
            rental.setCar(car);
            car.setInventory(car.getInventory() - 1);
            carRepository.save(car);
            return rentalMapper.toDto(rentalRepository.save(rental));
        }
        throw new RentalException("Car inventory is 0");
    }
    
    @Override
    public List<RentalDto> getRentalsByParameters(RentalSearchParameters parameters) {
        Specification<Rental> specification = rentalSpecificationBuilder.build(parameters);
        List<Rental> rentals = rentalRepository.findAll(specification);
        return rentalMapper.toDtos(rentals);
    }
    
    @Override
    public RentalDto getRentalById(Long id, User user) {
        return rentalMapper.toDto(rentalRepository.findByUserAndId(user, id)
                                                  .orElseThrow(() -> new RuntimeException(
                                                          "Rental with id: "
                                                          + id
                                                          + " not found")));
    }
    
    @Override
    @Transactional
    public RentalDto returnRental(Long id, User user) {
        Rental rental = rentalRepository.findByUserAndId(user, id)
                                        .orElseThrow(
                                                () -> new RuntimeException("Rental with id: "
                                                                           + id
                                                                           + " not found"));
        rental.setActualReturnDate(LocalDate.now());
        rental.setActive(false);
        Car car = carRepository.findById(rental.getCar()
                                               .getId())
                               .orElseThrow(() -> new RuntimeException("Car with id: "
                                                                       + id
                                                                       + " not found"));
        car.setInventory(car.getInventory() + 1);
        carRepository.save(car);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }
}
