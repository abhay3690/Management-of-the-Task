package com.task.exception;


import com.task.payload.APIResponse;
import com.task.payload.APIValidation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentialsException(BadCredentialsException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIValidation<HashMap<String, String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        var errors = new HashMap<String,String>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            var fieldname = ((FieldError)error).getField();
            var errormessage = error.getDefaultMessage();
            errors.put(fieldname, errormessage);
        });
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(new APIValidation<>(errors,HttpStatus.NOT_ACCEPTABLE));

    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse<Void>> resourceNotFoundExceptionHandler(ResourceNotFoundException ex){
        String message = ex.getMessage();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new APIResponse<Void>(message, HttpStatus.NOT_FOUND));
    }

}