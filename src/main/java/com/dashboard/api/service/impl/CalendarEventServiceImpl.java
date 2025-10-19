// src/main/java/com/dashboard/api/service/impl/CalendarEventServiceImpl.java
package com.dashboard.api.service.impl;

import com.dashboard.api.dto.EventFiltersDto;
import com.dashboard.api.dto.request.CreateEventRequest;
import com.dashboard.api.dto.request.UpdateEventRequest;
import com.dashboard.api.dto.response.EventResponse;
import com.dashboard.api.dto.response.EventStatsResponse;
import com.dashboard.api.entity.CalendarEvent;
import com.dashboard.api.entity.User;
import com.dashboard.api.enums.EventCategory;
import com.dashboard.api.enums.EventPriority;
import com.dashboard.api.exception.EventNotFoundException;
import com.dashboard.api.exception.ValidationException;
import com.dashboard.api.mapper.CalendarEventMapper;
import com.dashboard.api.repository.CalendarEventRepository;
import com.dashboard.api.service.CalendarEventService;
import com.dashboard.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CalendarEventServiceImpl implements CalendarEventService {

    private final CalendarEventRepository eventRepository;
    private final CalendarEventMapper eventMapper;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {
        log.debug("Fetching all calendar events for current user");
        User currentUser = userService.getCurrentUser();
        List<CalendarEvent> events = eventRepository.findByUserOrderByStartTimeAsc(currentUser);
        return eventMapper.toResponseList(events);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getFilteredEvents(EventFiltersDto filters) {
        log.debug("Fetching filtered calendar events with criteria: {}", filters);
        User currentUser = userService.getCurrentUser();

        List<CalendarEvent> events = new ArrayList<>();

        try {
            // Start with all events if no specific filters, or build up based on filters
            if (hasOnlyDateFilters(filters)) {
                // Handle date-only filtering
                events = getEventsByDateRange(currentUser, filters);
            } else if (hasOnlySearchFilter(filters)) {
                // Handle search-only filtering
                events = eventRepository.findByUserAndSearchTerm(currentUser.getId(), filters.getSearch());
            } else if (hasOnlyCategoryFilter(filters)) {
                // Handle category-only filtering
                events = eventRepository.findByUserAndCategory(currentUser, filters.getCategory());
            } else if (hasOnlyPriorityFilter(filters)) {
                // Handle priority-only filtering
                events = eventRepository.findByUserAndPriority(currentUser, filters.getPriority());
            } else if (hasCategoryAndPriorityOnly(filters)) {
                // Handle category and priority filtering
                events = eventRepository.findByUserAndCategoryAndPriority(currentUser, filters.getCategory(), filters.getPriority());
            } else {
                // Complex filtering - get all user events and filter in memory
                events = eventRepository.findByUserOrderByStartTimeAsc(currentUser);
                events = applyFiltersInMemory(events, filters);
            }

            // Apply date range filtering if needed and not already applied
            if (!hasOnlyDateFilters(filters) && (filters.getStartDate() != null || filters.getEndDate() != null)) {
                events = applyDateFiltering(events, filters);
            }

            // Apply search filtering if needed and not already applied
            if (!hasOnlySearchFilter(filters) && filters.getSearch() != null && !filters.getSearch().trim().isEmpty()) {
                events = applySearchFiltering(events, filters.getSearch());
            }

            // Sort by start time
            events.sort(Comparator.comparing(CalendarEvent::getStartTime));

        } catch (Exception e) {
            log.error("Error filtering events, falling back to get all user events", e);
            events = eventRepository.findByUserOrderByStartTimeAsc(currentUser);
        }

        return eventMapper.toResponseList(events);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(String id) {
        log.debug("Fetching calendar event by id: {}", id);
        User currentUser = userService.getCurrentUser();
        CalendarEvent event = findEventByIdAndUser(id, currentUser);
        return eventMapper.toResponse(event);
    }

    @Override
    public EventResponse createEvent(CreateEventRequest request) {
        log.debug("Creating new calendar event: {}", request.getTitle());
        User currentUser = userService.getCurrentUser();

        validateEventTiming(request.getStartTime(), request.getEndTime());

        CalendarEvent event = eventMapper.toEntity(request);
        event.setUser(currentUser); // Set the current user

        CalendarEvent savedEvent = eventRepository.save(event);
        log.info("Created new calendar event with id: {} for user: {}", savedEvent.getId(), currentUser.getEmail());
        return eventMapper.toResponse(savedEvent);
    }

    @Override
    public EventResponse updateEvent(String id, UpdateEventRequest request) {
        log.debug("Updating calendar event with id: {}", id);
        User currentUser = userService.getCurrentUser();
        CalendarEvent existingEvent = findEventByIdAndUser(id, currentUser);

        if (request.getStartTime() != null && request.getEndTime() != null) {
            validateEventTiming(request.getStartTime(), request.getEndTime());
        }

        eventMapper.updateEntity(request, existingEvent);
        CalendarEvent savedEvent = eventRepository.save(existingEvent);
        log.info("Updated calendar event with id: {} for user: {}", id, currentUser.getEmail());
        return eventMapper.toResponse(savedEvent);
    }

    @Override
    public void deleteEvent(String id) {
        log.debug("Deleting calendar event with id: {}", id);
        User currentUser = userService.getCurrentUser();
        CalendarEvent event = findEventByIdAndUser(id, currentUser);
        eventRepository.delete(event);
        log.info("Deleted calendar event with id: {} for user: {}", id, currentUser.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public EventStatsResponse getEventStats() {
        log.debug("Calculating calendar event statistics for current user");
        User currentUser = userService.getCurrentUser();

        long total = eventRepository.countByUser(currentUser);
        LocalDate today = LocalDate.now();
        long todayCount = eventRepository.countTodaysEventsByUser(currentUser.getId(), today);
        LocalDateTime now = LocalDateTime.now();
        long upcoming = eventRepository.countUpcomingEventsByUser(currentUser, now, now.plusDays(7));
        long overdue = eventRepository.countOverdueEventsByUser(currentUser, now);

        Map<String, Integer> byCategory = new HashMap<>();
        for (EventCategory category : EventCategory.values()) {
            byCategory.put(category.name().toLowerCase(),
                    (int) eventRepository.countByUserAndCategory(currentUser, category));
        }

        Map<String, Integer> byPriority = new HashMap<>();
        for (EventPriority priority : EventPriority.values()) {
            byPriority.put(priority.name().toLowerCase(),
                    (int) eventRepository.countByUserAndPriority(currentUser, priority));
        }

        double completionRate = total > 0 ? ((double) (total - overdue) / total) * 100 : 0.0;

        return EventStatsResponse.builder()
                .totalEvents((int) total)
                .todayEvents((int) todayCount)
                .upcomingEvents((int) upcoming)
                .overdueEvents((int) overdue)
                .byCategory(byCategory)
                .byPriority(byPriority)
                .completionRate(completionRate)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getTodaysEvents() {
        log.debug("Fetching today's calendar events for current user");
        User currentUser = userService.getCurrentUser();
        LocalDate today = LocalDate.now();
        List<CalendarEvent> events = eventRepository.findTodaysEventsByUser(currentUser.getId(), today);
        return eventMapper.toResponseList(events);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getUpcomingEvents(int days) {
        log.debug("Fetching upcoming calendar events for {} days for current user", days);
        User currentUser = userService.getCurrentUser();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(days);
        List<CalendarEvent> events = eventRepository.findUpcomingEventsByUser(currentUser, now, futureDate);
        return eventMapper.toResponseList(events);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getConflictingEvents(String eventId, CreateEventRequest request) {
        log.debug("Checking for conflicting events for current user");
        User currentUser = userService.getCurrentUser();
        List<CalendarEvent> conflicts = eventRepository.findConflictingEventsByUser(
                currentUser, eventId, request.getStartTime(), request.getEndTime());
        return eventMapper.toResponseList(conflicts);
    }

    // Helper methods
    private boolean hasOnlyDateFilters(EventFiltersDto filters) {
        return (filters.getStartDate() != null || filters.getEndDate() != null) &&
                filters.getCategory() == null &&
                filters.getPriority() == null &&
                (filters.getSearch() == null || filters.getSearch().trim().isEmpty());
    }

    private boolean hasOnlySearchFilter(EventFiltersDto filters) {
        return (filters.getSearch() != null && !filters.getSearch().trim().isEmpty()) &&
                filters.getCategory() == null &&
                filters.getPriority() == null &&
                filters.getStartDate() == null &&
                filters.getEndDate() == null;
    }

    private boolean hasOnlyCategoryFilter(EventFiltersDto filters) {
        return filters.getCategory() != null &&
                filters.getPriority() == null &&
                (filters.getSearch() == null || filters.getSearch().trim().isEmpty()) &&
                filters.getStartDate() == null &&
                filters.getEndDate() == null;
    }

    private boolean hasOnlyPriorityFilter(EventFiltersDto filters) {
        return filters.getPriority() != null &&
                filters.getCategory() == null &&
                (filters.getSearch() == null || filters.getSearch().trim().isEmpty()) &&
                filters.getStartDate() == null &&
                filters.getEndDate() == null;
    }

    private boolean hasCategoryAndPriorityOnly(EventFiltersDto filters) {
        return filters.getCategory() != null &&
                filters.getPriority() != null &&
                (filters.getSearch() == null || filters.getSearch().trim().isEmpty()) &&
                filters.getStartDate() == null &&
                filters.getEndDate() == null;
    }

    private List<CalendarEvent> getEventsByDateRange(User user, EventFiltersDto filters) {
        LocalDateTime startDateTime = filters.getStartDate() != null ?
                filters.getStartDate().atStartOfDay() : null;
        LocalDateTime endDateTime = filters.getEndDate() != null ?
                filters.getEndDate().atTime(23, 59, 59) : null;

        if (startDateTime != null && endDateTime != null) {
            return eventRepository.findByUserAndDateRange(user, startDateTime, endDateTime);
        } else if (startDateTime != null) {
            return eventRepository.findByUserAndStartDateAfter(user, startDateTime);
        } else if (endDateTime != null) {
            return eventRepository.findByUserAndEndDateBefore(user, endDateTime);
        }
        return eventRepository.findByUserOrderByStartTimeAsc(user);
    }

    private List<CalendarEvent> applyFiltersInMemory(List<CalendarEvent> events, EventFiltersDto filters) {
        return events.stream()
                .filter(event -> filters.getCategory() == null || event.getCategory().equals(filters.getCategory()))
                .filter(event -> filters.getPriority() == null || event.getPriority().equals(filters.getPriority()))
                .collect(Collectors.toList());
    }

    private List<CalendarEvent> applyDateFiltering(List<CalendarEvent> events, EventFiltersDto filters) {
        LocalDateTime startDateTime = filters.getStartDate() != null ?
                filters.getStartDate().atStartOfDay() : null;
        LocalDateTime endDateTime = filters.getEndDate() != null ?
                filters.getEndDate().atTime(23, 59, 59) : null;

        return events.stream()
                .filter(event -> startDateTime == null || event.getStartTime().isAfter(startDateTime) || event.getStartTime().isEqual(startDateTime))
                .filter(event -> endDateTime == null || event.getStartTime().isBefore(endDateTime) || event.getStartTime().isEqual(endDateTime))
                .collect(Collectors.toList());
    }

    private List<CalendarEvent> applySearchFiltering(List<CalendarEvent> events, String search) {
        String searchLower = search.toLowerCase().trim();
        return events.stream()
                .filter(event ->
                        (event.getTitle() != null && event.getTitle().toLowerCase().contains(searchLower)) ||
                                (event.getDescription() != null && event.getDescription().toLowerCase().contains(searchLower)) ||
                                (event.getLocation() != null && event.getLocation().toLowerCase().contains(searchLower))
                )
                .collect(Collectors.toList());
    }

    private CalendarEvent findEventByIdAndUser(String id, User user) {
        return eventRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new EventNotFoundException("Calendar event not found with id: " + id));
    }

    private void validateEventTiming(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new ValidationException("Event end time must be after start time");
        }
    }
}