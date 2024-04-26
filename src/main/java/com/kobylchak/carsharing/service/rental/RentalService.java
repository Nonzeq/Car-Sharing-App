package com.kobylchak.carsharing.service.rental;

import com.kobylchak.carsharing.dto.rental.CreateRentalRequestDto;
import com.kobylchak.carsharing.dto.rental.RentalDto;
import com.kobylchak.carsharing.dto.rental.RentalSearchParameters;
import com.kobylchak.carsharing.model.User;
import java.util.List;

public interface RentalService {
    RentalDto createRental(CreateRentalRequestDto requestDto, User user);
    
    List<RentalDto> getRentalsByParameters(RentalSearchParameters parameters);
    
    RentalDto getRentalById(Long id, User user);
    
    RentalDto returnRental(Long id, User user);
}
