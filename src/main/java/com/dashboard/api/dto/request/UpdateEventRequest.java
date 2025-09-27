// src/main/java/com/dashboard/api/dto/request/UpdateEventRequest.java
package com.dashboard.api.dto.request;

import com.dashboard.api.enums.EventCategory;
import com.dashboard.api.enums.EventPriority;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventRequest {

    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private EventCategory category;

    private EventPriority priority;

    private String location;

    private List<String> attendees;

    private String color;

    private Boolean isAllDay;
}