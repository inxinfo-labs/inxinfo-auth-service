package com.satishlabs.payment.service;

import com.satishlabs.payment.dto.request.CreatePaymentOrderRequest;
import com.satishlabs.payment.dto.request.VerifyPaymentRequest;
import com.satishlabs.payment.dto.response.PaymentOrderResponse;
import com.satishlabs.payment.dto.response.PaymentResponse;

public interface PaymentService {

	/**
	 * Create a Razorpay order and persist payment record (idempotent by idempotencyKey).
	 */
	PaymentOrderResponse createPaymentOrder(CreatePaymentOrderRequest request, Long userId, String idempotencyKey);

	/**
	 * Verify Razorpay signature and capture payment; update status to SUCCESS.
	 */
	PaymentResponse verifyAndCapture(VerifyPaymentRequest request, Long userId);

	PaymentResponse getByOrderId(String orderId, Long userId);
}
