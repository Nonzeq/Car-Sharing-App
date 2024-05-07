package com.kobylchak.carsharing.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SearchPaymentParameters {
    @JsonProperty("user_id")
    private Long userId;
}
