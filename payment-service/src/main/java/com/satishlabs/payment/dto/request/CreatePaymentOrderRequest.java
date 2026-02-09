package com.satishlabs.payment.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePaymentOrderRequest {

	@NotBlank(message = "orderId is required")
	private String orderId;

	@NotNull(message = "amount is required")
	@DecimalMin(value = "0.01", message = "amount must be positive")
	private BigDecimal amount;

	private String currency = "INR";

	private String receipt; // optional; default to orderId
}
