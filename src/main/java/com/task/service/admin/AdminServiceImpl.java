package com.task.service.admin;

import com.task.dto.CommentDto;
import com.task.dto.TaskDto;
import com.task.dto.UserDto;
import com.task.entity.Comment;
import com.task.entity.Task;
import com.task.entity.User;
import com.task.enums.TaskStatus;
import com.task.enums.UserRole;
import com.task.exception.ResourceNotFoundException;
import com.task.repository.CommentRepository;
import com.task.repository.TaskRepository;
import com.task.repository.UserRepository;
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
public class AdminServiceImpl implements AdminService{

    private final UserRepository userRepository;

    private final TaskRepository taskRepository;

    private final JwtUtil jwtUtil;

    private final CommentRepository commentRepository;

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getUserRole()== UserRole.EMPLOYEE)
                .map(User::getUserDto)
                .toList();
    }

    @Override
    public TaskDto createTask(TaskDto taskDto) {
        Optional<User> optionalUser = userRepository.findById(taskDto.getEmployeeId());

        if (optionalUser.isEmpty()){
            throw new ResourceNotFoundException("user","id",taskDto.getEmployeeId());
        }

            Task task = new Task();
            task.setTitle(taskDto.getTitle());
            task.setDescription(taskDto.getDescription());
            task.setDueDate(taskDto.getDueDate());
            task.setPriority(taskDto.getPriority());
            task.setTaskStatus(TaskStatus.PENDING);
            task.setUser(optionalUser.get());
            Task createdTask = taskRepository.save(task);
            return createdTask.getTaskDto();

    }

    @Override
    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Task::getDueDate).reversed())
                .map(Task::getTaskDto)
                .toList();
    }

    @Override
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("task", "id", id));
        taskRepository.delete(task);
    }

    @Override
    public TaskDto getTaskById(Long taskId) {
        return taskRepository.findById(taskId).map(Task::getTaskDto).orElseThrow(() -> new ResourceNotFoundException("task", "id", taskId));
    }

    @Override
    public TaskDto updateTask(Long taskId,TaskDto taskDto) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        Optional<User> optionalUser = userRepository.findById(taskDto.getEmployeeId());

        if (optionalTask.isEmpty()){
            throw new ResourceNotFoundException("task","id",taskId);
        }
        if (optionalUser.isEmpty()){
            throw new ResourceNotFoundException("user","id",taskDto.getEmployeeId());
        }

        Task existingTask = optionalTask.get();
        User user = optionalUser.get();
        existingTask.setTitle(taskDto.getTitle());
        existingTask.setPriority(taskDto.getPriority());
        existingTask.setDueDate(taskDto.getDueDate());
        existingTask.setDescription(taskDto.getDescription());
        existingTask.setTaskStatus(mapStringToTaskStatus(String.valueOf(taskDto.getTaskStatus())));
        existingTask.setUser(user);
        Task updatedTask = taskRepository.save(existingTask);
        return updatedTask.getTaskDto();
    }

    @Override
    public List<TaskDto> searchTaskByTitle(String title) {
        return taskRepository.findAllByTitleContaining(title)
                .stream()
                .sorted(Comparator.comparing(Task::getDueDate).reversed())
                .map(Task::getTaskDto)
                .toList();
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
        return commentRepository.findAllByTaskId(taskId).stream().map(Comment::getCommentDto).toList();
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
