package com.task.dto;

import com.task.enums.UserRole;
import lombok.Data;

@Data
public class UserDto {

    private Long id;

    private String email;

    private String name;

    private String password;

    private UserRole userRole;
}
