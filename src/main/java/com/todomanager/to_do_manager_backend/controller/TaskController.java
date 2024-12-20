package com.todomanager.to_do_manager_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.todomanager.to_do_manager_backend.model.Task;
import com.todomanager.to_do_manager_backend.repository.TaskRepository;

import jakarta.validation.Valid;

@RestController
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    @PostMapping("/tasks")
public Task createTask(@Valid @RequestBody Task task) {
    System.out.println("Received Task: " + task);
    return taskRepository.save(task);
}
    @GetMapping("/tasks")
    public List<Task> getTasks() {
        return taskRepository.findAll();
    }
    @GetMapping("/tasks/{id}")
    public Task getTask(@PathVariable Long id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));
    }
    @PutMapping("/tasks/{id}")
    public Task updateTask(@PathVariable Long id, @Valid @RequestBody Task updatedTask) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));
        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setCompleted(updatedTask.isCompleted());
        return taskRepository.save(task);
    }

    @DeleteMapping("/tasks/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
    }
    @ExceptionHandler(RuntimeException.class)
public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
}
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<String> handleValidationErrors(MethodArgumentNotValidException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation failed: " + ex.getMessage());
}
}
