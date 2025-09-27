// src/main/java/com/dashboard/api/dto/response/EventStatsResponse.java
package com.dashboard.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventStatsResponse {

    private int totalEvents;
    private int upcomingEvents;
    private int overdueEvents;
    private int todayEvents;
    private Map<String, Integer> byCategory;
    private Map<String, Integer> byPriority;
    private double completionRate;
}
