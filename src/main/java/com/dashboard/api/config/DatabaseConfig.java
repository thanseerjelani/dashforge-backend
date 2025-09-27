// src/main/java/com/dashboard/api/config/DatabaseConfig.java
package com.dashboard.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaAuditing
@EnableTransactionManagement
public class DatabaseConfig {
    // JPA Auditing configuration for automatic createdAt/updatedAt timestamps
}