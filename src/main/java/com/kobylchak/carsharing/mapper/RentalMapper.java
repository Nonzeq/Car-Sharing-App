package com.kobylchak.carsharing.mapper;

import com.kobylchak.carsharing.config.MapperConfig;
import com.kobylchak.carsharing.dto.rental.CreateRentalRequestDto;
import com.kobylchak.carsharing.dto.rental.RentalDto;
import com.kobylchak.carsharing.model.Rental;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class, uses = CarMapper.class)
public interface RentalMapper {
    Rental toModel(CreateRentalRequestDto requestDto);
    
    RentalDto toDto(Rental rental);
    
    List<RentalDto> toDtos(List<Rental> rentals);
}
