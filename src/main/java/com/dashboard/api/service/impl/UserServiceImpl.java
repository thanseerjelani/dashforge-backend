// src/main/java/com/dashboard/api/service/impl/UserServiceImpl.java
package com.dashboard.api.service.impl;

import com.dashboard.api.dto.request.RegisterRequest;
import com.dashboard.api.dto.request.UpdateProfileRequest;
import com.dashboard.api.dto.response.ProfileUpdateResponse;
import com.dashboard.api.dto.response.UserResponse;
import com.dashboard.api.entity.RefreshToken;
import com.dashboard.api.entity.User;
import com.dashboard.api.exception.UserAlreadyExistsException;
import com.dashboard.api.exception.UserNotFoundException;
import com.dashboard.api.exception.ValidationException;
import com.dashboard.api.mapper.UserMapper;
import com.dashboard.api.repository.UserRepository;
import com.dashboard.api.security.JwtUtils;
import com.dashboard.api.service.RefreshTokenService;
import com.dashboard.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenService refreshTokenService;

    private final JwtUtils jwtUtils;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Override
    public UserResponse createUser(RegisterRequest request) {
        log.debug("Creating new user with email: {}", request.getEmail());

        // Validate passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException("Passwords do not match");
        }

        // Check if user already exists
        if (existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User already exists with email: " + request.getEmail());
        }

        // Generate username from name (simple approach - you can make this more sophisticated)
        String username = generateUsername(request.getName());
        if (existsByUsername(username)) {
            username = generateUniqueUsername(request.getName());
        }

        // Map to entity and set additional fields
        User user = userMapper.toEntity(request);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Save user
        User savedUser = userRepository.save(user);
        log.info("Created new user with id: {} and email: {}", savedUser.getId(), savedUser.getEmail());

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserProfile(String userId) {
        log.debug("Getting user profile for id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            throw new UserNotFoundException("No authenticated user found");
        }

        String email = authentication.getName();
        return findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    private String generateUsername(String name) {
        // Simple username generation - lowercase, remove spaces
        return name.toLowerCase().replaceAll("\\s+", "");
    }

    private String generateUniqueUsername(String name) {
        String baseUsername = generateUsername(name);
        int counter = 1;
        String username = baseUsername + counter;

        while (existsByUsername(username)) {
            counter++;
            username = baseUsername + counter;
        }

        return username;
    }

    @Override
    public UserResponse updateProfile(UpdateProfileRequest request) {
        // Keep this simple method for backward compatibility
        ProfileUpdateResponse response = updateProfileWithTokenRefresh(request);
        return response.getUser();
    }

    @Override
    public ProfileUpdateResponse updateProfileWithTokenRefresh(UpdateProfileRequest request) {
        log.debug("Updating profile for current user with token refresh");

        User currentUser = getCurrentUser();

        // Store old values for comparison
        String oldName = currentUser.getName();
        String oldEmail = currentUser.getEmail();
        boolean emailChanged = !oldEmail.equals(request.getEmail());

        // Check if email is being changed and if it's already taken by another user
        if (emailChanged) {
            if (existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException("Email is already in use by another account");
            }
        }

        // Update name
        currentUser.setName(request.getName());

        // Update email
        currentUser.setEmail(request.getEmail());

        // Regenerate username if name changed
        if (!oldName.equals(request.getName())) {
            log.debug("Name changed from '{}' to '{}', regenerating username", oldName, request.getName());

            String newUsername = generateUsername(request.getName());

            if (existsByUsername(newUsername)) {
                newUsername = generateUniqueUsername(request.getName());
            }

            log.debug("New username: {}", newUsername);
            currentUser.setUsername(newUsername);
        }

        User savedUser = userRepository.save(currentUser);
        UserResponse userResponse = userMapper.toResponse(savedUser);

        ProfileUpdateResponse.ProfileUpdateResponseBuilder responseBuilder = ProfileUpdateResponse.builder()
                .user(userResponse)
                .emailChanged(emailChanged);

        // If email changed, generate new tokens immediately
        if (emailChanged) {
            log.info("Email changed from {} to {}. Generating new tokens.", oldEmail, request.getEmail());

            // 1. Revoke all old refresh tokens
            refreshTokenService.revokeAllUserTokens(savedUser);

            // 2. Generate new access token with updated email
            String newAccessToken = jwtUtils.generateAccessToken(savedUser);

            // 3. Generate and save new refresh token
            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(savedUser);

            // 4. Add new tokens to response
            responseBuilder
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken.getToken())
                    .tokenType("Bearer")
                    .expiresIn(jwtUtils.getAccessTokenExpirationMs() / 1000);

            log.info("New tokens generated for user with updated email: {}", savedUser.getEmail());
        }

        log.info("Profile updated successfully for user: {}", savedUser.getEmail());

        return responseBuilder.build();
    }
}