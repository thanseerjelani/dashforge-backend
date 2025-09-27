// src/main/java/com/dashboard/api/dto/request/CreateTodoRequest.java
package com.dashboard.api.dto.request;

import com.dashboard.api.enums.TodoCategory;
import com.dashboard.api.enums.TodoPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateTodoRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String description;

    @NotNull(message = "Priority is required")
    private TodoPriority priority;

    @NotNull(message = "Category is required")
    private TodoCategory category;

    private LocalDate dueDate;

    private List<String> tags;
}