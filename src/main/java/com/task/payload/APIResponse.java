package com.task.payload;


import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class APIResponse<T> {

    private T data;

    private String message;

    private HttpStatus status;

    public APIResponse() {}

    public APIResponse(T data) {
        this.data = data;
    }

    public APIResponse(String message) {
        this.message = message;
    }

    public APIResponse(String message,HttpStatus status){
        this.message=message;
        this.status=status;
    }

    public APIResponse(T data,HttpStatus status){
        this.data=data;
        this.status=status;
    }

    public APIResponse(T data,String message,HttpStatus status) {
        this.data = data;
        this.message = message;
        this.status=status;
    }

}
