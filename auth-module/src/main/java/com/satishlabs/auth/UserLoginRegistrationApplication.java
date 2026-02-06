package com.satishlabs.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.satishlabs")
@EnableJpaRepositories(basePackages = "com.satishlabs")
public class UserLoginRegistrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserLoginRegistrationApplication.class, args);
	}

}
