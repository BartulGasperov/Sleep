package com.noom.interview.fullstack.sleep.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;



@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = {AppException.class})
    @ResponseBody
    public ResponseEntity<ApiError> handleException(AppException exception){
        return ResponseEntity.status(exception.getCode())
                .body(ApiError.builder().message(exception.getMessage()).build());
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    @ResponseBody
    public ResponseEntity<ApiError> handleException(HttpMessageNotReadableException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                .body(ApiError.builder().message("Invalid body.").build());
    }

    @ExceptionHandler(value = {MissingRequestHeaderException.class})
    @ResponseBody
    public ResponseEntity<ApiError> handleException(MissingRequestHeaderException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                .body(ApiError.builder().message("Missing headers.").build());
    }
}

