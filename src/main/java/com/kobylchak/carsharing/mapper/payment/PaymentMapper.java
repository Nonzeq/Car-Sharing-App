package com.kobylchak.carsharing.mapper.payment;

import com.kobylchak.carsharing.config.MapperConfig;
import com.kobylchak.carsharing.dto.payment.PaymentDto;
import com.kobylchak.carsharing.model.Payment;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    PaymentDto toDto(Payment payment);
    
    List<PaymentDto> toDtos(List<Payment> payments);
}
