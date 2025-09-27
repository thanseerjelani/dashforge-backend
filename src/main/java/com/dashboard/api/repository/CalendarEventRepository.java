// src/main/java/com/dashboard/api/repository/CalendarEventRepository.java
// Alternative approach - using simpler queries and combining them in the service
package com.dashboard.api.repository;

import com.dashboard.api.entity.CalendarEvent;
import com.dashboard.api.enums.EventCategory;
import com.dashboard.api.enums.EventPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, String> {

    // Simple queries that work reliably
    List<CalendarEvent> findByCategory(EventCategory category);

    List<CalendarEvent> findByPriority(EventPriority priority);

    List<CalendarEvent> findByCategoryAndPriority(EventCategory category, EventPriority priority);

    // Search queries using native SQL
    @Query(value = "SELECT * FROM calendar_events WHERE " +
            "LOWER(title) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(description) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(location) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "ORDER BY start_time ASC",
            nativeQuery = true)
    List<CalendarEvent> findBySearchTerm(String searchTerm);

    // Date range queries
    @Query("SELECT e FROM CalendarEvent e WHERE e.startTime >= :startDate AND e.startTime <= :endDate ORDER BY e.startTime ASC")
    List<CalendarEvent> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT e FROM CalendarEvent e WHERE e.startTime >= :startDate ORDER BY e.startTime ASC")
    List<CalendarEvent> findByStartDateAfter(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT e FROM CalendarEvent e WHERE e.startTime <= :endDate ORDER BY e.startTime ASC")
    List<CalendarEvent> findByEndDateBefore(@Param("endDate") LocalDateTime endDate);

    // Today's events using native query
    @Query(value = "SELECT * FROM calendar_events e WHERE DATE(e.start_time) = ?1 ORDER BY e.start_time ASC",
            nativeQuery = true)
    List<CalendarEvent> findTodaysEvents(LocalDate today);

    // Find upcoming events
    @Query("SELECT e FROM CalendarEvent e WHERE e.startTime > :now AND e.startTime <= :futureDate ORDER BY e.startTime ASC")
    List<CalendarEvent> findUpcomingEvents(@Param("now") LocalDateTime now, @Param("futureDate") LocalDateTime futureDate);

    // Find overdue events
    @Query("SELECT e FROM CalendarEvent e WHERE e.endTime < :now")
    List<CalendarEvent> findOverdueEvents(@Param("now") LocalDateTime now);

    // Count by category
    long countByCategory(EventCategory category);

    // Count by priority
    long countByPriority(EventPriority priority);

    // Count today's events using native query
    @Query(value = "SELECT COUNT(*) FROM calendar_events e WHERE DATE(e.start_time) = ?1",
            nativeQuery = true)
    long countTodaysEvents(LocalDate today);

    // Count overdue events
    @Query("SELECT COUNT(e) FROM CalendarEvent e WHERE e.endTime < :now")
    long countOverdueEvents(@Param("now") LocalDateTime now);

    // Count upcoming events
    @Query("SELECT COUNT(e) FROM CalendarEvent e WHERE e.startTime > :now AND e.startTime <= :futureDate")
    long countUpcomingEvents(@Param("now") LocalDateTime now, @Param("futureDate") LocalDateTime futureDate);

    // Find events by specific date using native query
    @Query(value = "SELECT * FROM calendar_events e WHERE DATE(e.start_time) = ?1 ORDER BY e.start_time ASC",
            nativeQuery = true)
    List<CalendarEvent> findByDate(LocalDate date);

    // Check for conflicting events
    @Query("SELECT e FROM CalendarEvent e WHERE " +
            "(:eventId IS NULL OR e.id != :eventId) AND " +
            "((e.startTime <= :startTime AND e.endTime > :startTime) OR " +
            "(e.startTime < :endTime AND e.endTime >= :endTime) OR " +
            "(e.startTime >= :startTime AND e.endTime <= :endTime))")
    List<CalendarEvent> findConflictingEvents(
            @Param("eventId") String eventId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}