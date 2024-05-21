package com.kobylchak.carsharing.mapper;

import com.kobylchak.carsharing.config.MapperConfig;
import com.kobylchak.carsharing.dto.payment.PaymentDto;
import com.kobylchak.carsharing.model.Payment;
import com.kobylchak.carsharing.model.Rental;
import com.kobylchak.carsharing.model.enums.PaymentStatus;
import com.kobylchak.carsharing.model.enums.PaymentType;
import java.math.BigDecimal;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(target = "rentalId", source = "payment.rental.id")
    PaymentDto toDto(Payment payment);
    
    List<PaymentDto> toDtos(List<Payment> payments);
    
    Payment fromRentalToPayment(
            @Context Rental rental,
            @Context PaymentType type,
            @Context PaymentStatus status,
            BigDecimal amountToPay);
    
    @AfterMapping
    default void setRentalProperties(
            @MappingTarget Payment payment,
            @Context Rental rental,
            @Context PaymentType type,
            @Context PaymentStatus status) {
        payment.setRental(rental);
        payment.setStatus(status);
        payment.setType(type);
    }
}
