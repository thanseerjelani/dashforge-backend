// src/main/java/com/dashboard/api/service/impl/PasswordServiceImpl.java
package com.dashboard.api.service.impl;

import com.dashboard.api.dto.request.*;
import com.dashboard.api.dto.response.OtpResponse;
import com.dashboard.api.entity.Otp;
import com.dashboard.api.entity.User;
import com.dashboard.api.exception.InvalidCredentialsException;
import com.dashboard.api.exception.UserNotFoundException;
import com.dashboard.api.exception.ValidationException;
import com.dashboard.api.repository.OtpRepository;
import com.dashboard.api.repository.UserRepository;
import com.dashboard.api.service.EmailService;
import com.dashboard.api.service.PasswordService;
import com.dashboard.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PasswordServiceImpl implements PasswordService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final SecureRandom random = new SecureRandom();

    @Override
    public void changePassword(ChangePasswordRequest request) {
        log.debug("Processing password change request");

        User currentUser = userService.getCurrentUser();

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        // Validate new passwords match
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new ValidationException("New passwords do not match");
        }

        // Validate new password is different from current
        if (passwordEncoder.matches(request.getNewPassword(), currentUser.getPassword())) {
            throw new ValidationException("New password must be different from current password");
        }

        // Update password
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);

        // Send notification email
        try {
            emailService.sendPasswordChangedNotification(currentUser.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password changed notification", e);
        }

        log.info("Password changed successfully for user: {}", currentUser.getEmail());
    }

    @Override
    public OtpResponse sendPasswordResetOtp(ForgotPasswordRequest request) {
        log.debug("Sending password reset OTP to: {}", request.getEmail());

        // Check if user exists
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("No account found with this email"));

        // Delete any existing OTPs for this email
        otpRepository.deleteByEmail(request.getEmail());

        // Generate 6-digit OTP
        String otpCode = generateOtp();

        // Create OTP entity
        Otp otp = Otp.builder()
                .email(request.getEmail())
                .otpCode(otpCode)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .used(false)
                .build();

        otpRepository.save(otp);

        // Send OTP via email
        try {
            emailService.sendOtpEmail(request.getEmail(), otpCode);
            log.info("OTP sent via email to: {}", request.getEmail());
        } catch (Exception e) {
            log.error("Failed to send OTP email, logging to console as fallback", e);
            // Fallback: Log to console in case email fails
            System.out.println("=".repeat(50));
            System.out.println("PASSWORD RESET OTP: " + otpCode);
            System.out.println("Email: " + request.getEmail());
            System.out.println("Expires in: " + OTP_EXPIRY_MINUTES + " minutes");
            System.out.println("=".repeat(50));
        }

        return OtpResponse.builder()
                .message("OTP sent to your email")
                .email(maskEmail(request.getEmail()))
                .expiresIn(OTP_EXPIRY_MINUTES * 60)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyOtp(VerifyOtpRequest request) {
        log.debug("Verifying OTP for email: {}", request.getEmail());

        Otp otp = otpRepository.findByEmailAndOtpCodeAndUsedFalse(request.getEmail(), request.getOtp())
                .orElse(null);

        if (otp == null) {
            log.warn("Invalid OTP attempt for email: {}", request.getEmail());
            return false;
        }

        if (otp.isExpired()) {
            log.warn("Expired OTP attempt for email: {}", request.getEmail());
            return false;
        }

        log.info("OTP verified successfully for email: {}", request.getEmail());
        return true;
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        log.debug("Processing password reset for email: {}", request.getEmail());

        // Verify OTP first
        if (!verifyOtp(new VerifyOtpRequest() {{
            setEmail(request.getEmail());
            setOtp(request.getOtp());
        }})) {
            throw new ValidationException("Invalid or expired OTP");
        }

        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new ValidationException("Passwords do not match");
        }

        // Get user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Mark OTP as used
        otpRepository.markAsUsed(request.getEmail(), request.getOtp());

        // Send confirmation email
        try {
            emailService.sendPasswordResetConfirmation(request.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset confirmation", e);
        }

        log.info("Password reset successfully for user: {}", request.getEmail());
    }

    @Override
    public void cleanupExpiredOtps() {
        log.debug("Cleaning up expired OTPs");
        otpRepository.deleteExpiredOtps(LocalDateTime.now());
    }

    private String generateOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    private String maskEmail(String email) {
        String[] parts = email.split("@");
        if (parts.length != 2) return email;

        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 2) {
            return "**@" + domain;
        }

        String masked = username.charAt(0) + "***" + username.charAt(username.length() - 1);
        return masked + "@" + domain;
    }
}