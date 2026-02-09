package com.satishlabs.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderResponse {

	private Long paymentId;
	private String orderId;
	private String razorpayOrderId;
	private String razorpayKeyId; // public key for frontend checkout
	private BigDecimal amount;
	private String currency;
}
