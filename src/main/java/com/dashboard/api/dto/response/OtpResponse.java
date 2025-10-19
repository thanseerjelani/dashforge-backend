// src/main/java/com/dashboard/api/dto/response/OtpResponse.java
package com.dashboard.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpResponse {
    private String message;
    private String email;
    private long expiresIn; // seconds
}