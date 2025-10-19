// src/main/java/com/dashboard/api/repository/CalendarEventRepository.java
package com.dashboard.api.repository;

import com.dashboard.api.entity.CalendarEvent;
import com.dashboard.api.entity.User;
import com.dashboard.api.enums.EventCategory;
import com.dashboard.api.enums.EventPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, String> {

    // Find all events for a specific user
    List<CalendarEvent> findByUserOrderByStartTimeAsc(User user);

    // Find event by id and user (for security)
    Optional<CalendarEvent> findByIdAndUser(String id, User user);

    // Simple queries that work reliably with user context
    List<CalendarEvent> findByUserAndCategory(User user, EventCategory category);

    List<CalendarEvent> findByUserAndPriority(User user, EventPriority priority);

    List<CalendarEvent> findByUserAndCategoryAndPriority(User user, EventCategory category, EventPriority priority);

    // Search queries using native SQL with user context
    @Query(value = "SELECT * FROM calendar_events WHERE user_id = :userId AND (" +
            "LOWER(title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(location) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "ORDER BY start_time ASC",
            nativeQuery = true)
    List<CalendarEvent> findByUserAndSearchTerm(@Param("userId") String userId, @Param("searchTerm") String searchTerm);

    // Date range queries with user context
    @Query("SELECT e FROM CalendarEvent e WHERE e.user = :user AND e.startTime >= :startDate AND e.startTime <= :endDate ORDER BY e.startTime ASC")
    List<CalendarEvent> findByUserAndDateRange(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT e FROM CalendarEvent e WHERE e.user = :user AND e.startTime >= :startDate ORDER BY e.startTime ASC")
    List<CalendarEvent> findByUserAndStartDateAfter(@Param("user") User user, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT e FROM CalendarEvent e WHERE e.user = :user AND e.startTime <= :endDate ORDER BY e.startTime ASC")
    List<CalendarEvent> findByUserAndEndDateBefore(@Param("user") User user, @Param("endDate") LocalDateTime endDate);

    // Today's events using native query with user context
    @Query(value = "SELECT * FROM calendar_events e WHERE e.user_id = :userId AND DATE(e.start_time) = :today ORDER BY e.start_time ASC",
            nativeQuery = true)
    List<CalendarEvent> findTodaysEventsByUser(@Param("userId") String userId, @Param("today") LocalDate today);

    // Find upcoming events with user context
    @Query("SELECT e FROM CalendarEvent e WHERE e.user = :user AND e.startTime > :now AND e.startTime <= :futureDate ORDER BY e.startTime ASC")
    List<CalendarEvent> findUpcomingEventsByUser(@Param("user") User user, @Param("now") LocalDateTime now, @Param("futureDate") LocalDateTime futureDate);

    // Find overdue events with user context
    @Query("SELECT e FROM CalendarEvent e WHERE e.user = :user AND e.endTime < :now")
    List<CalendarEvent> findOverdueEventsByUser(@Param("user") User user, @Param("now") LocalDateTime now);

    // Count by category with user context
    long countByUserAndCategory(User user, EventCategory category);

    // Count by priority with user context
    long countByUserAndPriority(User user, EventPriority priority);

    // Count all events for user
    long countByUser(User user);

    // Count today's events using native query with user context
    @Query(value = "SELECT COUNT(*) FROM calendar_events e WHERE e.user_id = :userId AND DATE(e.start_time) = :today",
            nativeQuery = true)
    long countTodaysEventsByUser(@Param("userId") String userId, @Param("today") LocalDate today);

    // Count overdue events with user context
    @Query("SELECT COUNT(e) FROM CalendarEvent e WHERE e.user = :user AND e.endTime < :now")
    long countOverdueEventsByUser(@Param("user") User user, @Param("now") LocalDateTime now);

    // Count upcoming events with user context
    @Query("SELECT COUNT(e) FROM CalendarEvent e WHERE e.user = :user AND e.startTime > :now AND e.startTime <= :futureDate")
    long countUpcomingEventsByUser(@Param("user") User user, @Param("now") LocalDateTime now, @Param("futureDate") LocalDateTime futureDate);

    // Find events by specific date with user context using native query
    @Query(value = "SELECT * FROM calendar_events e WHERE e.user_id = :userId AND DATE(e.start_time) = :date ORDER BY e.start_time ASC",
            nativeQuery = true)
    List<CalendarEvent> findByUserAndDate(@Param("userId") String userId, @Param("date") LocalDate date);

    // Check for conflicting events with user context
    @Query("SELECT e FROM CalendarEvent e WHERE e.user = :user AND " +
            "(:eventId IS NULL OR e.id != :eventId) AND " +
            "((e.startTime <= :startTime AND e.endTime > :startTime) OR " +
            "(e.startTime < :endTime AND e.endTime >= :endTime) OR " +
            "(e.startTime >= :startTime AND e.endTime <= :endTime))")
    List<CalendarEvent> findConflictingEventsByUser(
            @Param("user") User user,
            @Param("eventId") String eventId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}