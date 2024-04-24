package com.kobylchak.carsharing.exception.handler;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ExceptionResponseData {
    private LocalDateTime timestamp;
    private HttpStatus httpStatus;
    private List<ErrorData> errorData;
    

}
