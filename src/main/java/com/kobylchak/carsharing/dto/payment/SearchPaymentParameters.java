package com.kobylchak.carsharing.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SearchPaymentParameters {
    @JsonProperty("user_id")
    @Positive
    private Long userId;
}
