package be.nexios.orders.controller;

import be.nexios.orders.service.OrderNotFoundException;
import be.nexios.orders.service.OrderStatusConflictException;
import be.nexios.orders.service.OrderValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OrderExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFound(OrderNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(OrderValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String badRequest(OrderValidationException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(OrderStatusConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String conflict(OrderStatusConflictException ex) {
        return ex.getMessage();
    }
}
