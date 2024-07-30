package com.task.payload;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class APIValidation<T>{

    private T message;

    private HttpStatus status;

    public APIValidation(T message) {
        this.message = message;
    }

    public APIValidation(T message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
