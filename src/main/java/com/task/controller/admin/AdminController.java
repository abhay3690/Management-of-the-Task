package com.task.controller.admin;

import com.task.dto.CommentDto;
import com.task.dto.TaskDto;
import com.task.dto.UserDto;
import com.task.entity.Task;
import com.task.entity.User;
import com.task.exception.ResourceNotFoundException;
import com.task.payload.APIResponse;
import com.task.repository.TaskRepository;
import com.task.repository.UserRepository;
import com.task.service.admin.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsers(){
        return ResponseEntity.ok(adminService.getUsers());
    }

    @PostMapping("/task")
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskDto taskDto){
        TaskDto createdTaskDto = adminService.createTask(taskDto);
        if (createdTaskDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTaskDto);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskDto>> getAllTasks(){
        return  ResponseEntity.status(HttpStatus.OK).body(adminService.getAllTasks());
    }

    @DeleteMapping("/task/{id}")
    public ResponseEntity<APIResponse<Void>> deleteTask(@PathVariable Long id){
        adminService.deleteTask(id);
        return ResponseEntity.status(HttpStatus.OK).body(new APIResponse<>(null,"Deleted SuccessFully",HttpStatus.OK));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long taskId){
        return ResponseEntity.ok(adminService.getTaskById(taskId));
    }

    @PutMapping("/task/{taskId}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long taskId,@Valid @RequestBody TaskDto taskDto){
        TaskDto updatedTask = adminService.updateTask(taskId, taskDto);

        if (updatedTask == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/tasks/search/{title}")
    public ResponseEntity<List<TaskDto>> searchTask(@PathVariable String title){
        return ResponseEntity.ok(adminService.searchTaskByTitle(title));
    }


    @PostMapping("/task/comment/{taskId}")
    public ResponseEntity<CommentDto> createComment(@PathVariable Long taskId,@RequestParam String content){
        CommentDto createdTaskDto = adminService.createComment(taskId, content);
        if (createdTaskDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTaskDto);
    }

    @GetMapping("/comments/{taskId}")
    public ResponseEntity<List<CommentDto>> getCommentsByTaskId(@PathVariable Long taskId){
        return ResponseEntity.ok(adminService.getCommentsByTaskId(taskId));
    }
}
