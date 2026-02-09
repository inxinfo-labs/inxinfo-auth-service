package com.satishlabs.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Payment service expects X-User-Id from API Gateway (JWT validated at gateway).
 * All /payments/** require the header; no JWT in this service.
 */
@Configuration
@EnableWebSecurity
public class PaymentSecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(a -> a.anyRequest().permitAll());
		return http.build();
	}
}
