// src/main/java/com/dashboard/api/repository/TodoRepository.java
package com.dashboard.api.repository;

import com.dashboard.api.entity.Todo;
import com.dashboard.api.entity.User;
import com.dashboard.api.enums.TodoCategory;
import com.dashboard.api.enums.TodoPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, String> {

    // Find all todos for a specific user
    List<Todo> findByUserOrderByCreatedAtDesc(User user);

    // Find todos by user and completion status
    List<Todo> findByUserAndCompleted(User user, Boolean completed);

    // Find todos by user and category
    List<Todo> findByUserAndCategory(User user, TodoCategory category);

    // Find todos by user and priority
    List<Todo> findByUserAndPriority(User user, TodoPriority priority);

    // Find todo by id and user (for security)
    Optional<Todo> findByIdAndUser(String id, User user);

    // Find overdue todos for a specific user
    @Query("SELECT t FROM Todo t WHERE t.user = :user AND t.completed = false AND t.dueDate < :currentDate")
    List<Todo> findOverdueTodosByUser(@Param("user") User user, @Param("currentDate") LocalDate currentDate);

    // Count todos by user and completion status
    long countByUserAndCompleted(User user, Boolean completed);

    // Count todos by user and category
    long countByUserAndCategory(User user, TodoCategory category);

    // Count todos by user and priority
    long countByUserAndPriority(User user, TodoPriority priority);

    // Count overdue todos for a specific user
    @Query("SELECT COUNT(t) FROM Todo t WHERE t.user = :user AND t.completed = false AND t.dueDate < :currentDate")
    long countOverdueTodosByUser(@Param("user") User user, @Param("currentDate") LocalDate currentDate);

    // Count all todos for a user
    long countByUser(User user);

    // Advanced filtering query with user context
    @Query("SELECT t FROM Todo t WHERE t.user = :user AND " +
            "(:category IS NULL OR t.category = :category) AND " +
            "(:priority IS NULL OR t.priority = :priority) AND " +
            "(:completed IS NULL OR t.completed = :completed) AND " +
            "(:search IS NULL OR :search = '' OR " +
            "LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "EXISTS (SELECT 1 FROM t.tags tag WHERE LOWER(tag) LIKE LOWER(CONCAT('%', :search, '%'))))" +
            " ORDER BY t.createdAt DESC")
    List<Todo> findFilteredTodosByUser(@Param("user") User user,
                                       @Param("category") TodoCategory category,
                                       @Param("priority") TodoPriority priority,
                                       @Param("completed") Boolean completed,
                                       @Param("search") String search);
}