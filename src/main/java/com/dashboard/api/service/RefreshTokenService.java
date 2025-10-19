// src/main/java/com/dashboard/api/service/RefreshTokenService.java
package com.dashboard.api.service;

import com.dashboard.api.entity.RefreshToken;
import com.dashboard.api.entity.User;

import java.util.Optional;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);

    Optional<RefreshToken> findByToken(String token);

    RefreshToken verifyExpiration(RefreshToken token);

    void revokeToken(String token);

    void revokeAllUserTokens(User user);

    void deleteExpiredTokens();
}