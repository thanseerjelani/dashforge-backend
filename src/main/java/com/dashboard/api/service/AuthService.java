// src/main/java/com/dashboard/api/service/AuthService.java
package com.dashboard.api.service;

import com.dashboard.api.dto.request.LoginRequest;
import com.dashboard.api.dto.request.RefreshTokenRequest;
import com.dashboard.api.dto.request.RegisterRequest;
import com.dashboard.api.dto.response.AuthResponse;
import com.dashboard.api.dto.response.RefreshTokenResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

    void logout(String refreshToken);

    void logoutAll(); // Logout from all devices
}