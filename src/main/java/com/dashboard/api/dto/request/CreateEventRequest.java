package com.dashboard.api.dto.request;

import com.dashboard.api.enums.EventCategory;
import com.dashboard.api.enums.EventPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateEventRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String description;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotNull(message = "Category is required")
    private EventCategory category;

    @NotNull(message = "Priority is required")
    private EventPriority priority;

    private String location;

    private List<String> attendees;

    @NotBlank(message = "Color is required")
    private String color;

    @Builder.Default
    private Boolean isAllDay = false;
}