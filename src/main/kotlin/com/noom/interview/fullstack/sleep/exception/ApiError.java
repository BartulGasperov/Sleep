package com.noom.interview.fullstack.sleep.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ApiError {
    private String message;
}
