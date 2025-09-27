// src/main/java/com/dashboard/api/repository/TodoRepository.java
package com.dashboard.api.repository;

import com.dashboard.api.entity.Todo;
import com.dashboard.api.enums.TodoCategory;
import com.dashboard.api.enums.TodoPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, String> {

    // Find todos by completion status
    List<Todo> findByCompleted(Boolean completed);

    // Find todos by category
    List<Todo> findByCategory(TodoCategory category);

    // Find todos by priority
    List<Todo> findByPriority(TodoPriority priority);

    // Find overdue todos
    @Query("SELECT t FROM Todo t WHERE t.completed = false AND t.dueDate < :currentDate")
    List<Todo> findOverdueTodos(@Param("currentDate") LocalDate currentDate);

    // Count todos by completion status
    long countByCompleted(Boolean completed);

    // Count todos by category
    long countByCategory(TodoCategory category);

    // Count todos by priority
    long countByPriority(TodoPriority priority);

    // Count overdue todos
    @Query("SELECT COUNT(t) FROM Todo t WHERE t.completed = false AND t.dueDate < :currentDate")
    long countOverdueTodos(@Param("currentDate") LocalDate currentDate);

    // Advanced filtering query
    @Query("SELECT t FROM Todo t WHERE " +
            "(:category IS NULL OR t.category = :category) AND " +
            "(:priority IS NULL OR t.priority = :priority) AND " +
            "(:completed IS NULL OR t.completed = :completed) AND " +
            "(:search IS NULL OR :search = '' OR " +
            "LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "EXISTS (SELECT 1 FROM t.tags tag WHERE LOWER(tag) LIKE LOWER(CONCAT('%', :search, '%'))))")
    List<Todo> findFilteredTodos(@Param("category") TodoCategory category,
                                 @Param("priority") TodoPriority priority,
                                 @Param("completed") Boolean completed,
                                 @Param("search") String search);
}