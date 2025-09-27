// src/main/java/com/dashboard/api/service/CalendarEventService.java
package com.dashboard.api.service;

import com.dashboard.api.dto.EventFiltersDto;
import com.dashboard.api.dto.request.CreateEventRequest;
import com.dashboard.api.dto.request.UpdateEventRequest;
import com.dashboard.api.dto.response.EventResponse;
import com.dashboard.api.dto.response.EventStatsResponse;

import java.util.List;

public interface CalendarEventService {

    List<EventResponse> getAllEvents();

    List<EventResponse> getFilteredEvents(EventFiltersDto filters);

    EventResponse getEventById(String id);

    EventResponse createEvent(CreateEventRequest request);

    EventResponse updateEvent(String id, UpdateEventRequest request);

    void deleteEvent(String id);

    EventStatsResponse getEventStats();

    List<EventResponse> getTodaysEvents();

    List<EventResponse> getUpcomingEvents(int days);

    List<EventResponse> getConflictingEvents(String eventId, CreateEventRequest request);
}