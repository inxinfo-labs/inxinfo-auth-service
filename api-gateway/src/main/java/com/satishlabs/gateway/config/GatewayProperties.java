package com.satishlabs.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "gateway")
public class GatewayProperties {

	private String authServiceUrl = "http://localhost:8081";
	private String pujaServiceUrl = "http://localhost:8082";
	private String panditServiceUrl = "http://localhost:8083";
	private String orderServiceUrl = "http://localhost:8084";
	private String notificationServiceUrl = "http://localhost:8085";
	private String paymentServiceUrl = "http://localhost:8086";
}
