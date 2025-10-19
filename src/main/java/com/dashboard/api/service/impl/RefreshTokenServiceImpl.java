// src/main/java/com/dashboard/api/service/impl/RefreshTokenServiceImpl.java
package com.dashboard.api.service.impl;

import com.dashboard.api.entity.RefreshToken;
import com.dashboard.api.entity.User;
import com.dashboard.api.exception.InvalidTokenException;
import com.dashboard.api.repository.RefreshTokenRepository;
import com.dashboard.api.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-token-expiration:604800000}") // 7 days in milliseconds
    private long refreshTokenExpirationMs;

    @Override
    public RefreshToken createRefreshToken(User user) {
        log.debug("Creating refresh token for user: {}", user.getEmail());

        // Revoke existing tokens for the user (optional - for single device login)
        // revokeAllUserTokens(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000))
                .revoked(false)
                .build();

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        log.info("Created refresh token for user: {}", user.getEmail());

        return savedToken;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new InvalidTokenException("Refresh token expired. Please sign in again");
        }
        if (token.getRevoked()) {
            throw new InvalidTokenException("Refresh token has been revoked. Please sign in again");
        }
        return token;
    }

    @Override
    public void revokeToken(String token) {
        log.debug("Revoking refresh token");
        refreshTokenRepository.revokeToken(token);
    }

    @Override
    public void revokeAllUserTokens(User user) {
        log.debug("Revoking all refresh tokens for user: {}", user.getEmail());
        refreshTokenRepository.revokeAllUserTokens(user);
    }

    @Override
    public void deleteExpiredTokens() {
        log.debug("Deleting expired refresh tokens");
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}