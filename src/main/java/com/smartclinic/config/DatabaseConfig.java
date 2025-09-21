package com.smartclinic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Database configuration for Smart Clinic application
 * Configures both MySQL (JPA) and MongoDB repositories with proper package separation
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.smartclinic.repository.mysql")
@EnableMongoRepositories(basePackages = "com.smartclinic.repository.mongodb")
public class DatabaseConfig {
    // Spring Boot auto-configuration handles the rest
}