package com.kobylchak.carsharing.exception.handler;

import lombok.Data;

@Data
public class FieldErrorData extends ErrorData {
    private String field;
    private Object cause;
}
