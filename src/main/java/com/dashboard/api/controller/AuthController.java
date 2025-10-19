// src/main/java/com/dashboard/api/controller/AuthController.java
package com.dashboard.api.controller;

import com.dashboard.api.dto.request.*;
import com.dashboard.api.dto.response.*;
import com.dashboard.api.service.AuthService;
import com.dashboard.api.service.PasswordService;
import com.dashboard.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:3011",
        "https://dashforge.netlify.app"
})
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final PasswordService passwordService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /api/auth/register - email: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login - email: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("POST /api/auth/refresh");
        RefreshTokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("POST /api/auth/logout");
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }

    @PostMapping("/logout-all")
    public ResponseEntity<ApiResponse<String>> logoutAll() {
        log.info("POST /api/auth/logout-all");
        authService.logoutAll();
        return ResponseEntity.ok(ApiResponse.success("Logged out from all devices", null));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile() {
        log.info("GET /api/auth/profile");
        UserResponse user = userService.getUserProfile(userService.getCurrentUser().getId());
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    // ===== Profile and Password =====

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileUpdateResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        log.info("PUT /api/auth/profile - updating profile");

        ProfileUpdateResponse response = userService.updateProfileWithTokenRefresh(request);

        if (response.isEmailChanged()) {
            // Email changed - new tokens provided
            return ResponseEntity.ok(ApiResponse.success(
                    "Profile updated successfully. New authentication tokens provided.",
                    response
            ));
        }

        // Only name changed - no new tokens needed
        return ResponseEntity.ok(ApiResponse.success(
                "Profile updated successfully",
                response
        ));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        log.info("POST /api/auth/change-password");
        passwordService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<OtpResponse>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("POST /api/auth/forgot-password - email: {}", request.getEmail());
        OtpResponse response = passwordService.sendPasswordResetOtp(request);
        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully", response));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Boolean>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        log.info("POST /api/auth/verify-otp - email: {}", request.getEmail());
        boolean isValid = passwordService.verifyOtp(request);
        if (isValid) {
            return ResponseEntity.ok(ApiResponse.success("OTP verified successfully", true));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid or expired OTP"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("POST /api/auth/reset-password - email: {}", request.getEmail());
        passwordService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", null));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth API is working!");
    }
}