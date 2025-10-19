// src/main/java/com/dashboard/api/service/impl/TodoServiceImpl.java
package com.dashboard.api.service.impl;

import com.dashboard.api.dto.TodoFiltersDto;
import com.dashboard.api.dto.request.CreateTodoRequest;
import com.dashboard.api.dto.request.UpdateTodoRequest;
import com.dashboard.api.dto.response.TodoResponse;
import com.dashboard.api.dto.response.TodoStatsResponse;
import com.dashboard.api.entity.Todo;
import com.dashboard.api.entity.User;
import com.dashboard.api.enums.TodoCategory;
import com.dashboard.api.enums.TodoPriority;
import com.dashboard.api.exception.TodoNotFoundException;
import com.dashboard.api.mapper.TodoMapper;
import com.dashboard.api.repository.TodoRepository;
import com.dashboard.api.service.TodoService;
import com.dashboard.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public List<TodoResponse> getAllTodos() {
        log.debug("Fetching all todos for current user");
        User currentUser = userService.getCurrentUser();
        List<Todo> todos = todoRepository.findByUserOrderByCreatedAtDesc(currentUser);
        return todoMapper.toResponseList(todos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoResponse> getFilteredTodos(TodoFiltersDto filters) {
        log.debug("Fetching filtered todos with criteria: {}", filters);
        User currentUser = userService.getCurrentUser();
        List<Todo> todos = todoRepository.findFilteredTodosByUser(
                currentUser,
                filters.getCategory(),
                filters.getPriority(),
                filters.getCompleted(),
                filters.getSearch()
        );
        return todoMapper.toResponseList(todos);
    }

    @Override
    @Transactional(readOnly = true)
    public TodoResponse getTodoById(String id) {
        log.debug("Fetching todo by id: {}", id);
        User currentUser = userService.getCurrentUser();
        Todo todo = findTodoByIdAndUser(id, currentUser);
        return todoMapper.toResponse(todo);
    }

    @Override
    public TodoResponse createTodo(CreateTodoRequest request) {
        log.debug("Creating new todo: {}", request.getTitle());
        User currentUser = userService.getCurrentUser();

        Todo todo = todoMapper.toEntity(request);
        todo.setUser(currentUser); // Set the current user

        Todo savedTodo = todoRepository.save(todo);
        log.info("Created new todo with id: {} for user: {}", savedTodo.getId(), currentUser.getEmail());
        return todoMapper.toResponse(savedTodo);
    }

    @Override
    public TodoResponse updateTodo(String id, UpdateTodoRequest request) {
        log.debug("Updating todo with id: {}", id);
        User currentUser = userService.getCurrentUser();
        Todo existingTodo = findTodoByIdAndUser(id, currentUser);

        todoMapper.updateEntity(request, existingTodo);
        Todo savedTodo = todoRepository.save(existingTodo);
        log.info("Updated todo with id: {} for user: {}", id, currentUser.getEmail());
        return todoMapper.toResponse(savedTodo);
    }

    @Override
    public void deleteTodo(String id) {
        log.debug("Deleting todo with id: {}", id);
        User currentUser = userService.getCurrentUser();
        Todo todo = findTodoByIdAndUser(id, currentUser);
        todoRepository.delete(todo);
        log.info("Deleted todo with id: {} for user: {}", id, currentUser.getEmail());
    }

    @Override
    public TodoResponse toggleTodo(String id) {
        log.debug("Toggling completion status for todo with id: {}", id);
        User currentUser = userService.getCurrentUser();
        Todo todo = findTodoByIdAndUser(id, currentUser);

        todo.setCompleted(!todo.getCompleted());
        Todo savedTodo = todoRepository.save(todo);
        log.info("Toggled completion status for todo with id: {} to {} for user: {}",
                id, savedTodo.getCompleted(), currentUser.getEmail());
        return todoMapper.toResponse(savedTodo);
    }

    @Override
    @Transactional(readOnly = true)
    public TodoStatsResponse getTodoStats() {
        log.debug("Calculating todo statistics for current user");
        User currentUser = userService.getCurrentUser();

        long total = todoRepository.countByUser(currentUser);
        long completed = todoRepository.countByUserAndCompleted(currentUser, true);
        long pending = total - completed;
        long overdue = todoRepository.countOverdueTodosByUser(currentUser, LocalDate.now());

        Map<String, Integer> byCategory = new HashMap<>();
        for (TodoCategory category : TodoCategory.values()) {
            byCategory.put(category.name().toLowerCase(),
                    (int) todoRepository.countByUserAndCategory(currentUser, category));
        }

        Map<String, Integer> byPriority = new HashMap<>();
        for (TodoPriority priority : TodoPriority.values()) {
            byPriority.put(priority.name().toLowerCase(),
                    (int) todoRepository.countByUserAndPriority(currentUser, priority));
        }

        return TodoStatsResponse.builder()
                .total((int) total)
                .completed((int) completed)
                .pending((int) pending)
                .overdue((int) overdue)
                .byCategory(byCategory)
                .byPriority(byPriority)
                .build();
    }

    private Todo findTodoByIdAndUser(String id, User user) {
        return todoRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new TodoNotFoundException("Todo not found with id: " + id));
    }
}