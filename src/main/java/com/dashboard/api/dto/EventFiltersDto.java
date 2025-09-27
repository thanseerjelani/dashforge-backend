// src/main/java/com/dashboard/api/dto/EventFiltersDto.java
package com.dashboard.api.dto;

import com.dashboard.api.enums.EventCategory;
import com.dashboard.api.enums.EventPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFiltersDto {

    private EventCategory category;
    private EventPriority priority;
    private String search;
    private LocalDate startDate;
    private LocalDate endDate;
}