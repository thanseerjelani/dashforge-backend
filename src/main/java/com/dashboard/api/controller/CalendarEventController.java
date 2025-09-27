// src/main/java/com/dashboard/api/controller/CalendarEventController.java
package com.dashboard.api.controller;

import com.dashboard.api.dto.EventFiltersDto;
import com.dashboard.api.dto.request.CreateEventRequest;
import com.dashboard.api.dto.request.UpdateEventRequest;
import com.dashboard.api.dto.response.ApiResponse;
import com.dashboard.api.dto.response.EventResponse;
import com.dashboard.api.dto.response.EventStatsResponse;
import com.dashboard.api.enums.EventCategory;
import com.dashboard.api.enums.EventPriority;
import com.dashboard.api.service.CalendarEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:3011",
        "https://dashforge.netlify.app"
})
public class CalendarEventController {

    private final CalendarEventService eventService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EventResponse>>> getAllEvents(
            @RequestParam(required = false) EventCategory category,
            @RequestParam(required = false) EventPriority priority,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /api/events - category: {}, priority: {}, search: {}, startDate: {}, endDate: {}",
                category, priority, search, startDate, endDate);

        EventFiltersDto filters = EventFiltersDto.builder()
                .category(category)
                .priority(priority)
                .search(search)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        List<EventResponse> events = hasFilters(filters) ?
                eventService.getFilteredEvents(filters) :
                eventService.getAllEvents();

        return ResponseEntity.ok(ApiResponse.success(events));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> getEventById(@PathVariable String id) {
        log.info("GET /api/events/{}", id);
        EventResponse event = eventService.getEventById(id);
        return ResponseEntity.ok(ApiResponse.success(event));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(@Valid @RequestBody CreateEventRequest request) {
        log.info("POST /api/events - title: {}", request.getTitle());
        EventResponse event = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Event created successfully", event));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(
            @PathVariable String id,
            @Valid @RequestBody UpdateEventRequest request) {
        log.info("PUT /api/events/{}", id);
        EventResponse event = eventService.updateEvent(id, request);
        return ResponseEntity.ok(ApiResponse.success("Event updated successfully", event));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable String id) {
        log.info("DELETE /api/events/{}", id);
        eventService.deleteEvent(id);
        return ResponseEntity.ok(ApiResponse.success("Event deleted successfully", null));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<EventStatsResponse>> getEventStats() {
        log.info("GET /api/events/stats");
        EventStatsResponse stats = eventService.getEventStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getTodaysEvents() {
        log.info("GET /api/events/today");
        List<EventResponse> events = eventService.getTodaysEvents();
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getUpcomingEvents(
            @RequestParam(defaultValue = "7") int days) {
        log.info("GET /api/events/upcoming - days: {}", days);
        List<EventResponse> events = eventService.getUpcomingEvents(days);
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    @PostMapping("/conflicts")
    public ResponseEntity<ApiResponse<List<EventResponse>>> checkConflicts(
            @RequestParam(required = false) String eventId,
            @Valid @RequestBody CreateEventRequest request) {
        log.info("POST /api/events/conflicts - eventId: {}", eventId);
        List<EventResponse> conflicts = eventService.getConflictingEvents(eventId, request);
        return ResponseEntity.ok(ApiResponse.success(conflicts));
    }

    private boolean hasFilters(EventFiltersDto filters) {
        return filters.getCategory() != null ||
                filters.getPriority() != null ||
                filters.getStartDate() != null ||
                filters.getEndDate() != null ||
                (filters.getSearch() != null && !filters.getSearch().trim().isEmpty());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Calendar API is working!");
    }
}