// src/main/java/com/dashboard/api/service/PasswordService.java
package com.dashboard.api.service;

import com.dashboard.api.dto.request.*;
import com.dashboard.api.dto.response.OtpResponse;

public interface PasswordService {

    void changePassword(ChangePasswordRequest request);

    OtpResponse sendPasswordResetOtp(ForgotPasswordRequest request);

    boolean verifyOtp(VerifyOtpRequest request);

    void resetPassword(ResetPasswordRequest request);

    void cleanupExpiredOtps();
}