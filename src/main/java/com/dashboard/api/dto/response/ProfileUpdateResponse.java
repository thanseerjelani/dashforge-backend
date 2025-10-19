// src/main/java/com/dashboard/api/dto/response/ProfileUpdateResponse.java
package com.dashboard.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateResponse {
    private UserResponse user;
    private String accessToken;  // New token if email changed
    private String refreshToken; // New refresh token if email changed
    @Builder.Default
    private String tokenType = "Bearer";
    private Long expiresIn;
    private boolean emailChanged; // Flag to tell frontend


}