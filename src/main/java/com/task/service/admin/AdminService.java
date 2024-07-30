package com.task.service.admin;

import com.task.dto.CommentDto;
import com.task.dto.TaskDto;
import com.task.dto.UserDto;

import java.util.List;

public interface AdminService {

    List<UserDto> getUsers();

    TaskDto createTask(TaskDto taskDto);

    List<TaskDto> getAllTasks();

    void deleteTask(Long id);

    List<TaskDto> searchTaskByTitle(String title);

    TaskDto updateTask(Long taskId,TaskDto taskDto);

    TaskDto getTaskById(Long taskId);

    CommentDto createComment(Long taskId,String content);

    List<CommentDto> getCommentsByTaskId(Long taskId);
}
