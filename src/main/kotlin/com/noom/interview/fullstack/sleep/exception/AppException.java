package com.noom.interview.fullstack.sleep.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException {

    private final HttpStatus code;

    public AppException(String message) {
        super(message);
        this.code = HttpStatus.BAD_REQUEST;
    }
}
