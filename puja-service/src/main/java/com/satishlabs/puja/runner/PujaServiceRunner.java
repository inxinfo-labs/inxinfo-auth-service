package com.satishlabs.puja.runner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.satishlabs.auth", "com.satishlabs.puja"})
@EnableJpaRepositories(basePackages = {"com.satishlabs.auth", "com.satishlabs.puja"})
public class PujaServiceRunner {

	public static void main(String[] args) {
		SpringApplication.run(PujaServiceRunner.class, args);
	}
}
