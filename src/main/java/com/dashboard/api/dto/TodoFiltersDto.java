// src/main/java/com/dashboard/api/dto/TodoFiltersDto.java
package com.dashboard.api.dto;

import com.dashboard.api.enums.TodoCategory;
import com.dashboard.api.enums.TodoPriority;
import lombok.Data;

@Data
public class TodoFiltersDto {

    private TodoCategory category;
    private TodoPriority priority;
    private Boolean completed;
    private String search;
}