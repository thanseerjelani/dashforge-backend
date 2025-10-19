
// src/main/java/com/dashboard/api/service/UserService.java
package com.dashboard.api.service;

import com.dashboard.api.dto.request.RegisterRequest;
import com.dashboard.api.dto.request.UpdateProfileRequest;
import com.dashboard.api.dto.response.ProfileUpdateResponse;
import com.dashboard.api.dto.response.UserResponse;
import com.dashboard.api.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserResponse createUser(RegisterRequest request);

    UserResponse getUserProfile(String userId);

    User getCurrentUser();

    User findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    UserResponse updateProfile(UpdateProfileRequest request);

    ProfileUpdateResponse updateProfileWithTokenRefresh(UpdateProfileRequest request);
}