package com.satishlabs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.satishlabs")
@EnableJpaRepositories(basePackages = "com.satishlabs")
public class InxinfoApplication {

	public static void main(String[] args) {
		SpringApplication.run(InxinfoApplication.class, args);
	}
}
