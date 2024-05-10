package com.kobylchak.carsharing.exception.handler;

import com.kobylchak.carsharing.exception.NotificationExcetion;
import com.kobylchak.carsharing.exception.PaymentException;
import com.kobylchak.carsharing.exception.RentalProcessingException;
import com.kobylchak.carsharing.exception.UserRegistrationException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({PaymentException.class})
    public ResponseEntity<Object> handlePaymentException(PaymentException exception) {
        ExceptionResponseData data = new ExceptionResponseData();
        data.setTimestamp(LocalDateTime.now());
        data.setHttpStatus(HttpStatus.BAD_REQUEST);
        data.setErrorData(List.of(new ErrorData(exception.getMessage())));
        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler({NotificationExcetion.class})
    public ResponseEntity<Object> handleAccessNotificationExcetion(NotificationExcetion exception) {
        ExceptionResponseData data = new ExceptionResponseData();
        data.setTimestamp(LocalDateTime.now());
        data.setHttpStatus(HttpStatus.BAD_REQUEST);
        data.setErrorData(List.of(new ErrorData(exception.getMessage())));
        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler({UserRegistrationException.class})
    public ResponseEntity<Object> handleAccessException(UserRegistrationException exception) {
        ExceptionResponseData data = new ExceptionResponseData();
        data.setTimestamp(LocalDateTime.now());
        data.setHttpStatus(HttpStatus.BAD_REQUEST);
        data.setErrorData(List.of(new ErrorData(exception.getMessage())));
        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler({RentalProcessingException.class})
    public ResponseEntity<Object> handleRentalException(RentalProcessingException exception) {
        ExceptionResponseData data = new ExceptionResponseData();
        data.setTimestamp(LocalDateTime.now());
        data.setHttpStatus(HttpStatus.BAD_REQUEST);
        data.setErrorData(List.of(new ErrorData(exception.getMessage())));
        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }
    
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        ExceptionResponseData data = new ExceptionResponseData();
        data.setTimestamp(LocalDateTime.now());
        data.setHttpStatus(HttpStatus.BAD_REQUEST);
        data.setErrorData(getErrorsData(ex));
        return new ResponseEntity<>(data, headers, status);
    }
    
    private List<ErrorData> getErrorsData(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getAllErrors().stream()
                .map(this::getError).toList();
        
    }
    
    private ErrorData getError(ObjectError error) {
        if (error instanceof FieldError fieldError) {
            FieldErrorData fieldErrorData = new FieldErrorData();
            fieldErrorData.setMessage(fieldError.getDefaultMessage());
            fieldErrorData.setField(fieldError.getField());
            fieldErrorData.setCause(fieldError.getRejectedValue());
            return fieldErrorData;
        }
        ErrorData errorData = new ErrorData();
        errorData.setMessage(error.getDefaultMessage());
        return errorData;
    }
}
