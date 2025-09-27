// src/main/java/com/dashboard/api/dto/request/UpdateTodoRequest.java
package com.dashboard.api.dto.request;

import com.dashboard.api.enums.TodoCategory;
import com.dashboard.api.enums.TodoPriority;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateTodoRequest {

    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String description;

    private Boolean completed;

    private TodoPriority priority;

    private TodoCategory category;

    private LocalDate dueDate;

    private List<String> tags;
}