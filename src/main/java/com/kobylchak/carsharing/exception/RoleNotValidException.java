package com.kobylchak.carsharing.exception;

public class RoleNotValidException extends RuntimeException {
    public RoleNotValidException(String message) {
        super(message);
    }
}
