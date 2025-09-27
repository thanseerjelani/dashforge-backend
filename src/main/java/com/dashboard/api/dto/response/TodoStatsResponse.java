// src/main/java/com/dashboard/api/dto/response/TodoStatsResponse.java
package com.dashboard.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class TodoStatsResponse {

    private Integer total;
    private Integer completed;
    private Integer pending;
    private Integer overdue;
    private Map<String, Integer> byCategory;
    private Map<String, Integer> byPriority;
}
