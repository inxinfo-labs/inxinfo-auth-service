package com.satishlabs.pandit.runner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.satishlabs.auth", "com.satishlabs.pandit"})
@EnableJpaRepositories(basePackages = {"com.satishlabs.auth", "com.satishlabs.pandit"})
public class PanditServiceRunner {

	public static void main(String[] args) {
		SpringApplication.run(PanditServiceRunner.class, args);
	}
}
