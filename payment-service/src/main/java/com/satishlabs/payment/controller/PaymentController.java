package com.satishlabs.payment.controller;

import com.satishlabs.payment.dto.request.CreatePaymentOrderRequest;
import com.satishlabs.payment.dto.request.VerifyPaymentRequest;
import com.satishlabs.payment.dto.response.PaymentOrderResponse;
import com.satishlabs.payment.dto.response.PaymentResponse;
import com.satishlabs.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

	private final PaymentService paymentService;

	private static final String HEADER_X_USER_ID = "X-User-Id";
	private static final String HEADER_IDEMPOTENCY_KEY = "Idempotency-Key";

	private Long getUserIdFromHeader(@RequestHeader(value = HEADER_X_USER_ID, required = false) String xUserId) {
		if (xUserId == null || xUserId.isBlank()) {
			throw new IllegalArgumentException("Missing " + HEADER_X_USER_ID + " (required for payment; use API Gateway)");
		}
		try {
			return Long.parseLong(xUserId.trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid " + HEADER_X_USER_ID);
		}
	}

	/**
	 * Create a Razorpay payment order. Returns razorpay_order_id and key for frontend checkout.
	 */
	@PostMapping("/create")
	public ResponseEntity<PaymentOrderResponse> create(
			@Valid @RequestBody CreatePaymentOrderRequest request,
			@RequestHeader(value = HEADER_X_USER_ID, required = false) String xUserId,
			@RequestHeader(value = HEADER_IDEMPOTENCY_KEY, required = false) String idempotencyKey) {
		Long userId = getUserIdFromHeader(xUserId);
		PaymentOrderResponse response = paymentService.createPaymentOrder(request, userId, idempotencyKey);
		return ResponseEntity.ok(response);
	}

	/**
	 * Verify Razorpay signature and capture payment. Call after checkout success.
	 */
	@PostMapping("/verify")
	public ResponseEntity<PaymentResponse> verify(
			@Valid @RequestBody VerifyPaymentRequest request,
			@RequestHeader(value = HEADER_X_USER_ID, required = false) String xUserId) {
		Long userId = getUserIdFromHeader(xUserId);
		PaymentResponse response = paymentService.verifyAndCapture(request, userId);
		return ResponseEntity.ok(response);
	}

	/**
	 * Get payment by order id (for display).
	 */
	@GetMapping("/order/{orderId}")
	public ResponseEntity<PaymentResponse> getByOrderId(
			@PathVariable String orderId,
			@RequestHeader(value = HEADER_X_USER_ID, required = false) String xUserId) {
		Long userId = getUserIdFromHeader(xUserId);
		PaymentResponse response = paymentService.getByOrderId(orderId, userId);
		return ResponseEntity.ok(response);
	}
}
