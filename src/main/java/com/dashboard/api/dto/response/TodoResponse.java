// src/main/java/com/dashboard/api/dto/response/TodoResponse.java
package com.dashboard.api.dto.response;

import com.dashboard.api.enums.TodoCategory;
import com.dashboard.api.enums.TodoPriority;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TodoResponse {

    private String id;
    private String title;
    private String description;
    private Boolean completed;
    private TodoPriority priority;
    private TodoCategory category;
    private LocalDate dueDate;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean overdue;
}