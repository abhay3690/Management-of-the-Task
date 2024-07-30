package com.task.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {

    @NotEmpty
    @Email
    @Column(unique = true)
    private String email;

    @NotEmpty(message = "name should not be empty")
    @NotBlank
    @Size(min = 2,message = "name must required 2 character")
    private String name;

    @NotEmpty(message = "password should not be empty")
    @NotBlank
    @Size(min = 5,message = "password must required 6 character")
    private String password;
}
