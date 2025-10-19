// src/main/java/com/dashboard/api/service/impl/AuthServiceImpl.java
package com.dashboard.api.service.impl;

import com.dashboard.api.dto.request.LoginRequest;
import com.dashboard.api.dto.request.RefreshTokenRequest;
import com.dashboard.api.dto.request.RegisterRequest;
import com.dashboard.api.dto.response.AuthResponse;
import com.dashboard.api.dto.response.RefreshTokenResponse;
import com.dashboard.api.dto.response.UserResponse;
import com.dashboard.api.entity.RefreshToken;
import com.dashboard.api.entity.User;
import com.dashboard.api.exception.InvalidCredentialsException;
import com.dashboard.api.exception.InvalidTokenException;
import com.dashboard.api.mapper.UserMapper;
import com.dashboard.api.security.JwtUtils;
import com.dashboard.api.service.AuthService;
import com.dashboard.api.service.RefreshTokenService;
import com.dashboard.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    public AuthResponse register(RegisterRequest request) {
        log.debug("Registering new user with email: {}", request.getEmail());

        // Create user
        UserResponse user = userService.createUser(request);

        // Get user entity for token generation
        User userEntity = userService.findByEmail(request.getEmail());

        // Generate tokens
        String accessToken = jwtUtils.generateAccessToken(userEntity);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userEntity);

        log.info("User registered successfully with email: {}", request.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtUtils.getAccessTokenExpirationMs() / 1000) // Convert to seconds
                .user(user)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.debug("Authenticating user with email: {}", request.getEmail());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = (User) authentication.getPrincipal();

            // Generate tokens
            String accessToken = jwtUtils.generateAccessToken(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            UserResponse userResponse = userMapper.toResponse(user);

            log.info("User authenticated successfully with email: {}", request.getEmail());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .tokenType("Bearer")
                    .expiresIn(jwtUtils.getAccessTokenExpirationMs() / 1000) // Convert to seconds
                    .user(userResponse)
                    .build();

        } catch (AuthenticationException e) {
            log.error("Authentication failed for email: {}", request.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        log.debug("Refreshing access token");

        return refreshTokenService.findByToken(request.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateAccessToken(user);
                    return RefreshTokenResponse.builder()
                            .accessToken(token)
                            .tokenType("Bearer")
                            .expiresIn(jwtUtils.getAccessTokenExpirationMs() / 1000) // Convert to seconds
                            .build();
                })
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));
    }

    @Override
    public void logout(String refreshToken) {
        log.debug("Logging out user");
        refreshTokenService.revokeToken(refreshToken);
    }

    @Override
    public void logoutAll() {
        log.debug("Logging out user from all devices");
        User currentUser = userService.getCurrentUser();
        refreshTokenService.revokeAllUserTokens(currentUser);
    }
}