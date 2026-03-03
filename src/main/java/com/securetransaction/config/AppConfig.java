package com.securetransaction.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Enables @Async on methods like EmailService.sendRegistrationEmail().
 * As in Java with @Async the email is sent in a background thread pool,
 */
@Configuration
@EnableAsync
public class AppConfig {
}
