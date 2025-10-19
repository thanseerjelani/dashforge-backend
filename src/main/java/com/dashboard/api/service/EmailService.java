// src/main/java/com/dashboard/api/service/EmailService.java
package com.dashboard.api.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otp);
    void sendPasswordResetConfirmation(String toEmail);
    void sendPasswordChangedNotification(String toEmail);
}