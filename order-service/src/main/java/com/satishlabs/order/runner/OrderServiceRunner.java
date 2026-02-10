package com.satishlabs.order.runner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.satishlabs.auth", "com.satishlabs.puja", "com.satishlabs.order"})
@EnableJpaRepositories(basePackages = {"com.satishlabs.auth", "com.satishlabs.puja", "com.satishlabs.order"})
public class OrderServiceRunner {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceRunner.class, args);
	}
}
