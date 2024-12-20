package com.todomanager.to_do_manager_backend;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.todomanager.to_do_manager_backend.controller.TaskController;
import com.todomanager.to_do_manager_backend.model.Task;
import com.todomanager.to_do_manager_backend.repository.TaskRepository;

class ToDoManagerBackendApplicationTests {

    private MockMvc mockMvc;
    private TaskRepository taskRepository;

    @BeforeEach
void setup() {
    // Mock the repository
    taskRepository = mock(TaskRepository.class);

    // Initialize MockMvc with the controller
    TaskController taskController = new TaskController(taskRepository);
    mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
}

    @Test
    void testGetAllTasks() throws Exception {
        when(taskRepository.findAll()).thenReturn(List.of(new Task(1L, "Test Task", "Test Description", false)));

        mockMvc.perform(get("/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(1))
            .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    void testPostTask() throws Exception {
        Task task = new Task(1L, "Test Task", "Test Description", false);
        when(taskRepository.save(Mockito.any(Task.class))).thenReturn(task);

        String jsonPayload = "{\"title\": \"Test Task\", \"description\": \"Test Description\", \"completed\": false}";

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Test Task"))
            .andExpect(jsonPath("$.description").value("Test Description"))
            .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void testGetTaskById() throws Exception {
        Task task = new Task(1L, "Task By ID", "Description for task by ID", false);
        when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));

        mockMvc.perform(get("/tasks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("Task By ID"))
            .andExpect(jsonPath("$.description").value("Description for task by ID"))
            .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void testUpdateTasks() throws Exception {
        Task originalTask = new Task(1L, "Original Task", "Original Description", false);
        Task updatedTask = new Task(1L, "Updated Task", "Updated Description", true);

        when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(originalTask));
        when(taskRepository.save(Mockito.any(Task.class))).thenReturn(updatedTask);

        String jsonPayload = "{\"title\": \"Updated Task\", \"description\": \"Updated Description\", \"completed\": true}";

        mockMvc.perform(put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("Updated Task"))
            .andExpect(jsonPath("$.description").value("Updated Description"))
            .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void testDeleteTask() throws Exception {
        // Mock the repository behavior for delete
        Mockito.doNothing().when(taskRepository).deleteById(1L);

        // Perform DELETE request
        mockMvc.perform(delete("/tasks/1"))
            .andExpect(status().isOk());

        // Verify the repository method was called
        verify(taskRepository, times(1)).deleteById(1L);
    }

	@Test
void testGetTaskByIdNotFound() throws Exception {
    when(taskRepository.findById(1L)).thenReturn(java.util.Optional.empty());

    mockMvc.perform(get("/tasks/1"))
        .andExpect(status().isNotFound());
}
@Test
void testUpdateTaskNotFound() throws Exception {
    when(taskRepository.findById(1L)).thenReturn(java.util.Optional.empty());

    String jsonPayload = "{\"title\": \"Updated Task\", \"description\": \"Updated Description\", \"completed\": true}";

    mockMvc.perform(put("/tasks/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonPayload))
        .andExpect(status().isNotFound());
}
@Test
void testPostTaskValidationError() throws Exception {
    String invalidJsonPayload = "{\"description\": \"This is a test description\"}"; // Missing 'title'

    mockMvc.perform(post("/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJsonPayload))
        .andExpect(status().isBadRequest());
}
@Test
void testUpdateTaskValidationError() throws Exception {
    Task originalTask = new Task(1L, "Original Task", "Original Description", false);
    when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(originalTask));

    String invalidJsonPayload = "{\"description\": \"Updated Description\"}"; // Missing title field

    mockMvc.perform(put("/tasks/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJsonPayload))
        .andExpect(status().isBadRequest());
}
}