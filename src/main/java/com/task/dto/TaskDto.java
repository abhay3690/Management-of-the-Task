package com.task.dto;

import com.task.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;

import java.util.Date;

@Data
public class TaskDto {

    private Long id;

    @NotEmpty(message = "title should not be empty")
    @NotBlank
    @Size(min = 2,message = "task must required 2 character")
    private String title;

    @NotEmpty(message = "Description should not be empty")
    @NotBlank
    @Size(min = 5,message = "Description must required 5 character")
    private String description;

    private Date dueDate;

    private String priority;

    private TaskStatus taskStatus;

    private Long employeeId;

    private String employeeName;
}
