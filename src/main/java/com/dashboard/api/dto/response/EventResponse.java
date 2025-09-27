// src/main/java/com/dashboard/api/dto/response/EventResponse.java
package com.dashboard.api.dto.response;

import com.dashboard.api.enums.EventCategory;
import com.dashboard.api.enums.EventPriority;
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
public class EventResponse {

    private String id;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
