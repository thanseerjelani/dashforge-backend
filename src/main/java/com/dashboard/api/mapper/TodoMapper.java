// src/main/java/com/dashboard/api/mapper/TodoMapper.java
package com.dashboard.api.mapper;

import com.dashboard.api.dto.request.CreateTodoRequest;
import com.dashboard.api.dto.request.UpdateTodoRequest;
import com.dashboard.api.dto.response.TodoResponse;
import com.dashboard.api.entity.Todo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TodoMapper {

    public Todo toEntity(CreateTodoRequest request) {
        Todo todo = Todo.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .category(request.getCategory())
                .dueDate(request.getDueDate())
                .completed(false)
                .tags(request.getTags() != null ? request.getTags() : new ArrayList<>())
                .build();
        return todo;
    }

    public TodoResponse toResponse(Todo todo) {
        TodoResponse response = new TodoResponse();
        response.setId(todo.getId());
        response.setTitle(todo.getTitle());
        response.setDescription(todo.getDescription());
        response.setCompleted(todo.getCompleted());
        response.setPriority(todo.getPriority());
        response.setCategory(todo.getCategory());
        response.setDueDate(todo.getDueDate());
        response.setTags(todo.getTags());
        response.setCreatedAt(todo.getCreatedAt());
        response.setUpdatedAt(todo.getUpdatedAt());
        response.setOverdue(todo.isOverdue());
        return response;
    }

    public List<TodoResponse> toResponseList(List<Todo> todos) {
        return todos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void updateEntity(UpdateTodoRequest request, Todo todo) {
        if (request.getTitle() != null) {
            todo.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            todo.setDescription(request.getDescription());
        }
        if (request.getCompleted() != null) {
            todo.setCompleted(request.getCompleted());
        }
        if (request.getPriority() != null) {
            todo.setPriority(request.getPriority());
        }
        if (request.getCategory() != null) {
            todo.setCategory(request.getCategory());
        }
        if (request.getDueDate() != null) {
            todo.setDueDate(request.getDueDate());
        }
        if (request.getTags() != null) {
            todo.setTags(request.getTags());
        }
    }
}