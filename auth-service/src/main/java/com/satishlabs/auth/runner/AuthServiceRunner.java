package com.satishlabs.auth.runner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Auth service – distributed mode. Port and DB from config (SERVER_PORT, SPRING_DATASOURCE_*).
 * Frontend and API Gateway call this via gateway at /api/auth and /api/user.
 */
@SpringBootApplication(scanBasePackages = "com.satishlabs.auth")
@ConfigurationPropertiesScan(basePackages = "com.satishlabs.auth.config")
public class AuthServiceRunner {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceRunner.class, args);
	}
}
