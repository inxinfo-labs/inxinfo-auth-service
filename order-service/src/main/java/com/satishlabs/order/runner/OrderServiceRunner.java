package com.satishlabs.order.runner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.satishlabs.auth", "com.satishlabs.puja", "com.satishlabs.order"})
public class OrderServiceRunner {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceRunner.class, args);
	}
}
