package com.satishlabs.payment.dto.response;

import com.satishlabs.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

	private Long id;
	private String orderId;
	private Long userId;
	private BigDecimal amount;
	private String currency;
	private PaymentStatus status;
	private String razorpayOrderId;
	private String razorpayPaymentId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
