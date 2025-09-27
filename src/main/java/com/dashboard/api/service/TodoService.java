// src/main/java/com/dashboard/api/service/TodoService.java
package com.dashboard.api.service;

import com.dashboard.api.dto.TodoFiltersDto;
import com.dashboard.api.dto.request.CreateTodoRequest;
import com.dashboard.api.dto.request.UpdateTodoRequest;
import com.dashboard.api.dto.response.TodoResponse;
import com.dashboard.api.dto.response.TodoStatsResponse;
import com.dashboard.api.entity.Todo;

import java.util.List;

public interface TodoService {

    List<TodoResponse> getAllTodos();

    List<TodoResponse> getFilteredTodos(TodoFiltersDto filters);

    TodoResponse getTodoById(String id);

    TodoResponse createTodo(CreateTodoRequest request);

    TodoResponse updateTodo(String id, UpdateTodoRequest request);

    void deleteTodo(String id);

    TodoResponse toggleTodo(String id);

    TodoStatsResponse getTodoStats();
}
