package com.satishlabs.puja.runner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.satishlabs.auth", "com.satishlabs.puja"})
public class PujaServiceRunner {

	public static void main(String[] args) {
		SpringApplication.run(PujaServiceRunner.class, args);
	}
}
