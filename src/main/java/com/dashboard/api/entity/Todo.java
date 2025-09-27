// src/main/java/com/dashboard/api/entity/Todo.java
package com.dashboard.api.entity;

import com.dashboard.api.enums.TodoCategory;
import com.dashboard.api.enums.TodoPriority;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "todos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Todo extends BaseEntity {

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Completed status cannot be null")
    @Column(nullable = false)
    @Builder.Default
    private Boolean completed = false;

    @NotNull(message = "Priority cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TodoPriority priority;

    @NotNull(message = "Category cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TodoCategory category;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "todo_tags", joinColumns = @JoinColumn(name = "todo_id"))
    @Column(name = "tag")
    private List<String> tags;

    // Helper method to check if todo is overdue
    public boolean isOverdue() {
        return !completed && dueDate != null && dueDate.isBefore(LocalDate.now());
    }
}