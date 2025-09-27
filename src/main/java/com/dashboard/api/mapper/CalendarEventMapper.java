// src/main/java/com/dashboard/api/mapper/CalendarEventMapper.java
package com.dashboard.api.mapper;

import com.dashboard.api.dto.request.CreateEventRequest;
import com.dashboard.api.dto.request.UpdateEventRequest;
import com.dashboard.api.dto.response.EventResponse;
import com.dashboard.api.entity.CalendarEvent;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CalendarEventMapper {

    EventResponse toResponse(CalendarEvent event);

    List<EventResponse> toResponseList(List<CalendarEvent> events);

    CalendarEvent toEntity(CreateEventRequest request);

    void updateEntity(UpdateEventRequest request, @MappingTarget CalendarEvent event);
}
