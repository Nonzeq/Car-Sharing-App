package com.kobylchak.carsharing.mapper.payment;

import com.kobylchak.carsharing.config.MapperConfig;
import com.kobylchak.carsharing.dto.payment.PaymentDto;
import com.kobylchak.carsharing.model.Payment;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(target = "rentalId", source = "payment.rental.id")
    PaymentDto toDto(Payment payment);
    
    List<PaymentDto> toDtos(List<Payment> payments);
}
