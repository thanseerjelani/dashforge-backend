// src/main/java/com/dashboard/api/scheduler/OtpCleanupScheduler.java
package com.dashboard.api.scheduler;

import com.dashboard.api.service.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OtpCleanupScheduler {

    private final PasswordService passwordService;

    // Run every hour
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredOtps() {
        log.info("Running scheduled OTP cleanup task");
        try {
            passwordService.cleanupExpiredOtps();
            log.info("Expired OTPs cleaned up successfully");
        } catch (Exception e) {
            log.error("Error cleaning up expired OTPs", e);
        }
    }
}