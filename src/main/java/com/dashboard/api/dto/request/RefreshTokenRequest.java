// src/main/java/com/dashboard/api/dto/request/RefreshTokenRequest.java
package com.dashboard.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}