package com.satishlabs.puja.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PujaConfig {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
