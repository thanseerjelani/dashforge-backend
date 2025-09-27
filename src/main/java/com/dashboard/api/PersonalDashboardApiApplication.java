// src/main/java/com/dashboard/api/PersonalDashboardApiApplication.java
package com.dashboard.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.dashboard.api.entity") // Explicitly specify where to find entities
@EnableJpaRepositories("com.dashboard.api.repository") // Explicitly specify where to find repositories
public class PersonalDashboardApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersonalDashboardApiApplication.class, args);
	}
}