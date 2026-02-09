package com.satishlabs.payment.config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {

	@Value("${razorpay.key_id:}")
	private String keyId;

	@Value("${razorpay.key_secret:}")
	private String keySecret;

	@Bean
	public RazorpayClient razorpayClient() throws RazorpayException {
		if (keyId == null || keyId.isBlank() || keySecret == null || keySecret.isBlank()) {
			throw new IllegalStateException(
				"Razorpay key_id and key_secret must both be set (e.g. RAZORPAY_KEY_ID, RAZORPAY_KEY_SECRET)");
		}
		return new RazorpayClient(keyId, keySecret);
	}

	@Bean
	public String razorpayKeyId() {
		return keyId != null ? keyId : "";
	}
}
