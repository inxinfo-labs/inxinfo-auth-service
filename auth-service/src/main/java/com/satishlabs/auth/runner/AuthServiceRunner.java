package com.satishlabs.auth.runner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Auth service – distributed mode. Port 8081, uses auth_db.
 * Frontend and API Gateway call this via gateway at /api/auth and /api/user.
 * Entities and repositories are discovered under com.satishlabs.auth via component scan.
 */
@SpringBootApplication(scanBasePackages = "com.satishlabs.auth")
public class AuthServiceRunner {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceRunner.class, args);
	}
}
