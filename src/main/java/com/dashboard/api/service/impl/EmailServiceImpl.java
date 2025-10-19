// src/main/java/com/dashboard/api/service/impl/EmailServiceImpl.java
package com.dashboard.api.service.impl;

import com.dashboard.api.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.name:DashForge}")
    private String appName;

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(appName + " - Password Reset OTP");
            message.setText(buildOtpEmailContent(otp));

            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    @Override
    public void sendPasswordResetConfirmation(String toEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(appName + " - Password Reset Successful");
            message.setText(buildPasswordResetConfirmationContent());

            mailSender.send(message);
            log.info("Password reset confirmation sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset confirmation to: {}", toEmail, e);
        }
    }

    @Override
    public void sendPasswordChangedNotification(String toEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(appName + " - Password Changed");
            message.setText(buildPasswordChangedContent());

            mailSender.send(message);
            log.info("Password changed notification sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password changed notification to: {}", toEmail, e);
        }
    }

    private String buildOtpEmailContent(String otp) {
        return String.format("""
            Hello,
            
            You requested to reset your password for your %s account.
            
            Your One-Time Password (OTP) is: %s
            
            This OTP is valid for 10 minutes.
            
            If you did not request this password reset, please ignore this email.
            
            Best regards,
            %s Team
            """, appName, otp, appName);
    }

    private String buildPasswordResetConfirmationContent() {
        return String.format("""
            Hello,
            
            Your password has been successfully reset for your %s account.
            
            If you did not perform this action, please contact support immediately.
            
            Best regards,
            %s Team
            """, appName, appName);
    }

    private String buildPasswordChangedContent() {
        return String.format("""
            Hello,
            
            Your password has been successfully changed for your %s account.
            
            If you did not perform this action, please contact support immediately and reset your password.
            
            Best regards,
            %s Team
            """, appName, appName);
    }
}