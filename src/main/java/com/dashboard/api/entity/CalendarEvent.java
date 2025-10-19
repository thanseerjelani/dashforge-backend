// src/main/java/com/dashboard/api/entity/CalendarEvent.java
package com.dashboard.api.entity;

import com.dashboard.api.enums.EventCategory;
import com.dashboard.api.enums.EventPriority;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "calendar_events", indexes = {
        @Index(name = "idx_event_user_id", columnList = "user_id"),
        @Index(name = "idx_event_start_time", columnList = "start_time"),
        @Index(name = "idx_event_end_time", columnList = "end_time"),
        @Index(name = "idx_event_category", columnList = "category"),
        @Index(name = "idx_event_priority", columnList = "priority")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarEvent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User cannot be null")
    private User user;

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Start time cannot be null")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @NotNull(message = "End time cannot be null")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @NotNull(message = "Category cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventCategory category;

    @NotNull(message = "Priority cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventPriority priority;

    @Column
    private String location;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "event_attendees", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "attendee")
    private List<String> attendees;

    @NotNull(message = "Color cannot be null")
    @Column(nullable = false)
    private String color;

    @NotNull(message = "All day status cannot be null")
    @Column(name = "is_all_day", nullable = false)
    @Builder.Default
    private Boolean isAllDay = false;

    // Helper methods
    public boolean isPast() {
        return endTime.isBefore(LocalDateTime.now());
    }

    public boolean isToday() {
        LocalDateTime now = LocalDateTime.now();
        return startTime.toLocalDate().equals(now.toLocalDate());
    }

    public boolean isUpcoming(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(days);
        return startTime.isAfter(now) && startTime.isBefore(futureDate);
    }

    public boolean isCurrent() {
        LocalDateTime now = LocalDateTime.now();
        return startTime.isBefore(now) && endTime.isAfter(now);
    }
}