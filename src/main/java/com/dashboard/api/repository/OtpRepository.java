// src/main/java/com/dashboard/api/repository/OtpRepository.java
package com.dashboard.api.repository;

import com.dashboard.api.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, String> {

    Optional<Otp> findByEmailAndOtpCodeAndUsedFalse(String email, String otpCode);

    Optional<Otp> findTopByEmailOrderByCreatedAtDesc(String email);

    @Modifying
    @Transactional
    @Query("UPDATE Otp o SET o.used = true WHERE o.email = :email AND o.otpCode = :otpCode")
    void markAsUsed(@Param("email") String email, @Param("otpCode") String otpCode);

    @Modifying
    @Transactional
    @Query("DELETE FROM Otp o WHERE o.expiresAt < :now")
    void deleteExpiredOtps(@Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query("DELETE FROM Otp o WHERE o.email = :email")
    void deleteByEmail(@Param("email") String email);
}