package com.kobylchak.carsharing.repository.rental;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RentalKeyParameters {
    USER("user"),
    IS_ACTIVE("isActive");
    private final String key;
}
