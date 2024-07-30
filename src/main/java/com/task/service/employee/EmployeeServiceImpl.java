package com.task.service.employee;

import com.task.dto.CommentDto;
import com.task.dto.TaskDto;
import com.task.entity.Comment;
import com.task.entity.Task;
import com.task.entity.User;
import com.task.enums.TaskStatus;
import com.task.exception.ResourceNotFoundException;
import com.task.repository.CommentRepository;
import com.task.repository.TaskRepository;
import com.task.utils.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService{

    private final TaskRepository taskRepository;

    private final JwtUtil jwtUtil;

    private final CommentRepository commentRepository;

    @Override
    public List<TaskDto> getTasksByUserId() {
        User user = jwtUtil.getLoggedInUser();
        if (user != null){
               return taskRepository.findAllByUserId(user.getId())
                    .stream()
                    .sorted(Comparator.comparing(Task::getDueDate).reversed())
                    .map(Task::getTaskDto)
                    .toList();
        }
        throw new EntityNotFoundException("User not found");
    }

    @Override
    public TaskDto updateTask(Long id, String status) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()){
            Task existingTask = optionalTask.get();
            existingTask.setTaskStatus(mapStringToTaskStatus(status));
            Task updatedTask = taskRepository.save(existingTask);
            return updatedTask.getTaskDto();
        }
        throw new EntityNotFoundException("Task not found");
    }

    @Override
    public TaskDto getTaskById(Long taskId) {
        return taskRepository.findById(taskId).map(Task::getTaskDto).orElseThrow(() -> new ResourceNotFoundException("task", "id", taskId));
    }

    @Override
    public CommentDto createComment(Long taskId, String content) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        User user = jwtUtil.getLoggedInUser();
        if (optionalTask.isPresent() && user!= null){
            Comment comment = new Comment();
            comment.setCreateAt(new Date());
            comment.setContent(content);
            comment.setTask(optionalTask.get());
            comment.setUser(user);
            return commentRepository.save(comment).getCommentDto();
        }
        throw new EntityNotFoundException("User or Task not found");
    }


    @Override
    public List<CommentDto> getCommentsByTaskId(Long taskId) {
        User user = jwtUtil.getLoggedInUser();
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task!=null && task.getUser().getId().equals(user.getId())){
            return commentRepository.findAllByTaskId(taskId).stream().map(Comment::getCommentDto).toList();
        }
        throw new ResourceNotFoundException("task","id",taskId);
    }


    private TaskStatus mapStringToTaskStatus(String status){
        return switch (status){
            case "PENDING" -> TaskStatus.PENDING;
            case "INPROGRESS" -> TaskStatus.INPROGRESS;
            case "COMPLETED" -> TaskStatus.COMPLETED;
            case "DEFERRED" -> TaskStatus.DEFERRED;
            default -> TaskStatus.CANCELLED;
        };
    }
}
