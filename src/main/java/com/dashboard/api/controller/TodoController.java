// src/main/java/com/dashboard/api/controller/TodoController.java
package com.dashboard.api.controller;

import com.dashboard.api.dto.TodoFiltersDto;
import com.dashboard.api.dto.request.CreateTodoRequest;
import com.dashboard.api.dto.request.UpdateTodoRequest;
import com.dashboard.api.dto.response.ApiResponse;
import com.dashboard.api.dto.response.TodoResponse;
import com.dashboard.api.dto.response.TodoStatsResponse;
import com.dashboard.api.enums.TodoCategory;
import com.dashboard.api.enums.TodoPriority;
import com.dashboard.api.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3011"})// Vite + backup for CRA
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TodoResponse>>> getAllTodos(
            @RequestParam(required = false) TodoCategory category,
            @RequestParam(required = false) TodoPriority priority,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) String search) {

        log.info("GET /api/todos - category: {}, priority: {}, completed: {}, search: {}",
                category, priority, completed, search);

        TodoFiltersDto filters = new TodoFiltersDto();
        filters.setCategory(category);
        filters.setPriority(priority);
        filters.setCompleted(completed);
        filters.setSearch(search);

        // If no filters provided, get all todos
        List<TodoResponse> todos = hasFilters(filters) ?
                todoService.getFilteredTodos(filters) :
                todoService.getAllTodos();

        return ResponseEntity.ok(ApiResponse.success(todos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoResponse>> getTodoById(@PathVariable String id) {
        log.info("GET /api/todos/{}", id);
        TodoResponse todo = todoService.getTodoById(id);
        return ResponseEntity.ok(ApiResponse.success(todo));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TodoResponse>> createTodo(@Valid @RequestBody CreateTodoRequest request) {
        log.info("POST /api/todos - title: {}", request.getTitle());
        TodoResponse todo = todoService.createTodo(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Todo created successfully", todo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoResponse>> updateTodo(
            @PathVariable String id,
            @Valid @RequestBody UpdateTodoRequest request) {
        log.info("PUT /api/todos/{}", id);
        TodoResponse todo = todoService.updateTodo(id, request);
        return ResponseEntity.ok(ApiResponse.success("Todo updated successfully", todo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTodo(@PathVariable String id) {
        log.info("DELETE /api/todos/{}", id);
        todoService.deleteTodo(id);
        return ResponseEntity.ok(ApiResponse.success("Todo deleted successfully", null));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<TodoResponse>> toggleTodo(@PathVariable String id) {
        log.info("PATCH /api/todos/{}/toggle", id);
        TodoResponse todo = todoService.toggleTodo(id);
        return ResponseEntity.ok(ApiResponse.success("Todo status toggled successfully", todo));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<TodoStatsResponse>> getTodoStats() {
        log.info("GET /api/todos/stats");
        TodoStatsResponse stats = todoService.getTodoStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    private boolean hasFilters(TodoFiltersDto filters) {
        return filters.getCategory() != null ||
                filters.getPriority() != null ||
                filters.getCompleted() != null ||
                (filters.getSearch() != null && !filters.getSearch().trim().isEmpty());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Todo API is working!");
    }
}