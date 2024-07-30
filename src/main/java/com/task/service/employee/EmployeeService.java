package com.task.service.employee;

import com.task.dto.CommentDto;
import com.task.dto.TaskDto;

import java.util.List;

public interface EmployeeService {

    List<TaskDto> getTasksByUserId();

    TaskDto updateTask(Long id,String status);

    TaskDto getTaskById(Long taskId);

    CommentDto createComment(Long taskId, String content);

    List<CommentDto> getCommentsByTaskId(Long taskId);
}
