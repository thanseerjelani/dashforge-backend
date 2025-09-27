// src/main/java/com/dashboard/api/service/impl/CalendarEventServiceImpl.java
package com.dashboard.api.service.impl;

import com.dashboard.api.dto.EventFiltersDto;
import com.dashboard.api.dto.request.CreateEventRequest;
import com.dashboard.api.dto.request.UpdateEventRequest;
import com.dashboard.api.dto.response.EventResponse;
import com.dashboard.api.dto.response.EventStatsResponse;
import com.dashboard.api.entity.CalendarEvent;
import com.dashboard.api.enums.EventCategory;
import com.dashboard.api.enums.EventPriority;
import com.dashboard.api.exception.EventNotFoundException;
import com.dashboard.api.exception.ValidationException;
import com.dashboard.api.mapper.CalendarEventMapper;
import com.dashboard.api.repository.CalendarEventRepository;
import com.dashboard.api.service.CalendarEventService;
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

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {
        log.debug("Fetching all calendar events");
        List<CalendarEvent> events = eventRepository.findAll();
        return eventMapper.toResponseList(events);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getFilteredEvents(EventFiltersDto filters) {
        log.debug("Fetching filtered calendar events with criteria: {}", filters);

        List<CalendarEvent> events = new ArrayList<>();

        try {
            // Start with all events if no specific filters, or build up based on filters
            if (hasOnlyDateFilters(filters)) {
                // Handle date-only filtering
                events = getEventsByDateRange(filters);
            } else if (hasOnlySearchFilter(filters)) {
                // Handle search-only filtering
                events = eventRepository.findBySearchTerm(filters.getSearch());
            } else if (hasOnlyCategoryFilter(filters)) {
                // Handle category-only filtering
                events = eventRepository.findByCategory(filters.getCategory());
            } else if (hasOnlyPriorityFilter(filters)) {
                // Handle priority-only filtering
                events = eventRepository.findByPriority(filters.getPriority());
            } else if (hasCategoryAndPriorityOnly(filters)) {
                // Handle category and priority filtering
                events = eventRepository.findByCategoryAndPriority(filters.getCategory(), filters.getPriority());
            } else {
                // Complex filtering - get all events and filter in memory
                events = eventRepository.findAll();
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
            log.error("Error filtering events, falling back to get all events", e);
            events = eventRepository.findAll();
        }

        return eventMapper.toResponseList(events);
    }

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

    private List<CalendarEvent> getEventsByDateRange(EventFiltersDto filters) {
        LocalDateTime startDateTime = filters.getStartDate() != null ?
                filters.getStartDate().atStartOfDay() : null;
        LocalDateTime endDateTime = filters.getEndDate() != null ?
                filters.getEndDate().atTime(23, 59, 59) : null;

        if (startDateTime != null && endDateTime != null) {
            return eventRepository.findByDateRange(startDateTime, endDateTime);
        } else if (startDateTime != null) {
            return eventRepository.findByStartDateAfter(startDateTime);
        } else if (endDateTime != null) {
            return eventRepository.findByEndDateBefore(endDateTime);
        }
        return eventRepository.findAll();
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

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(String id) {
        log.debug("Fetching calendar event by id: {}", id);
        CalendarEvent event = findEventById(id);
        return eventMapper.toResponse(event);
    }

    @Override
    public EventResponse createEvent(CreateEventRequest request) {
        log.debug("Creating new calendar event: {}", request.getTitle());

        validateEventTiming(request.getStartTime(), request.getEndTime());

        CalendarEvent event = eventMapper.toEntity(request);
        CalendarEvent savedEvent = eventRepository.save(event);
        log.info("Created new calendar event with id: {}", savedEvent.getId());
        return eventMapper.toResponse(savedEvent);
    }

    @Override
    public EventResponse updateEvent(String id, UpdateEventRequest request) {
        log.debug("Updating calendar event with id: {}", id);
        CalendarEvent existingEvent = findEventById(id);

        if (request.getStartTime() != null && request.getEndTime() != null) {
            validateEventTiming(request.getStartTime(), request.getEndTime());
        }

        eventMapper.updateEntity(request, existingEvent);
        CalendarEvent savedEvent = eventRepository.save(existingEvent);
        log.info("Updated calendar event with id: {}", id);
        return eventMapper.toResponse(savedEvent);
    }

    @Override
    public void deleteEvent(String id) {
        log.debug("Deleting calendar event with id: {}", id);
        CalendarEvent event = findEventById(id);
        eventRepository.delete(event);
        log.info("Deleted calendar event with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public EventStatsResponse getEventStats() {
        log.debug("Calculating calendar event statistics");

        long total = eventRepository.count();
        LocalDate today = LocalDate.now();
        long todayCount = eventRepository.countTodaysEvents(today);
        LocalDateTime now = LocalDateTime.now();
        long upcoming = eventRepository.countUpcomingEvents(now, now.plusDays(7));
        long overdue = eventRepository.countOverdueEvents(now);

        Map<String, Integer> byCategory = new HashMap<>();
        for (EventCategory category : EventCategory.values()) {
            byCategory.put(category.name().toLowerCase(),
                    (int) eventRepository.countByCategory(category));
        }

        Map<String, Integer> byPriority = new HashMap<>();
        for (EventPriority priority : EventPriority.values()) {
            byPriority.put(priority.name().toLowerCase(),
                    (int) eventRepository.countByPriority(priority));
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
        log.debug("Fetching today's calendar events");
        LocalDate today = LocalDate.now();
        List<CalendarEvent> events = eventRepository.findTodaysEvents(today);
        return eventMapper.toResponseList(events);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getUpcomingEvents(int days) {
        log.debug("Fetching upcoming calendar events for {} days", days);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(days);
        List<CalendarEvent> events = eventRepository.findUpcomingEvents(now, futureDate);
        return eventMapper.toResponseList(events);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getConflictingEvents(String eventId, CreateEventRequest request) {
        log.debug("Checking for conflicting events");
        List<CalendarEvent> conflicts = eventRepository.findConflictingEvents(
                eventId, request.getStartTime(), request.getEndTime());
        return eventMapper.toResponseList(conflicts);
    }

    private CalendarEvent findEventById(String id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Calendar event not found with id: " + id));
    }

    private void validateEventTiming(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new ValidationException("Event end time must be after start time");
        }
    }
}