package com.reqflowly.application.common.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidStateException(InvalidStateException exception) {
        return new ResponseEntity<>(new ApiErrorResponse(exception.getMessage(), exception.getParams()), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
