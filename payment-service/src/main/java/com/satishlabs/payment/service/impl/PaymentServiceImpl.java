package com.satishlabs.payment.service.impl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.satishlabs.payment.dto.request.CreatePaymentOrderRequest;
import com.satishlabs.payment.dto.request.VerifyPaymentRequest;
import com.satishlabs.payment.dto.response.PaymentOrderResponse;
import com.satishlabs.payment.dto.response.PaymentResponse;
import com.satishlabs.payment.entity.Payment;
import com.satishlabs.payment.entity.PaymentStatus;
import com.satishlabs.payment.entity.Transaction;
import com.satishlabs.payment.repository.PaymentRepository;
import com.satishlabs.payment.repository.TransactionRepository;
import com.satishlabs.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final TransactionRepository transactionRepository;
	private final RazorpayClient razorpayClient;
	private final String razorpayKeyId;
	private final String razorpayKeySecret;

	public PaymentServiceImpl(PaymentRepository paymentRepository,
	                          TransactionRepository transactionRepository,
	                          RazorpayClient razorpayClient,
	                          @Value("${razorpay.key_id:}") String razorpayKeyId,
	                          @Value("${razorpay.key_secret:}") String razorpayKeySecret) {
		this.paymentRepository = paymentRepository;
		this.transactionRepository = transactionRepository;
		this.razorpayClient = razorpayClient;
		this.razorpayKeyId = razorpayKeyId != null ? razorpayKeyId : "";
		this.razorpayKeySecret = razorpayKeySecret != null ? razorpayKeySecret : "";
	}

	@Override
	@Transactional
	public PaymentOrderResponse createPaymentOrder(CreatePaymentOrderRequest request, Long userId, String idempotencyKey) {
		if (idempotencyKey != null && !idempotencyKey.isBlank()) {
			Optional<Payment> existing = paymentRepository.findByIdempotencyKey(idempotencyKey);
			if (existing.isPresent()) {
				Payment p = existing.get();
				return PaymentOrderResponse.builder()
					.paymentId(p.getId())
					.orderId(p.getOrderId())
					.razorpayOrderId(p.getRazorpayOrderId())
					.razorpayKeyId(razorpayKeyId)
					.amount(p.getAmount())
					.currency(p.getCurrency())
					.build();
			}
		}

		// Amount in paise (Razorpay expects integer)
		int amountPaise = request.getAmount().multiply(BigDecimal.valueOf(100)).intValue();
		String receipt = request.getReceipt() != null && !request.getReceipt().isBlank()
			? request.getReceipt() : request.getOrderId();
		String currency = request.getCurrency() != null ? request.getCurrency() : "INR";

		JSONObject orderRequest = new JSONObject();
		orderRequest.put("amount", amountPaise);
		orderRequest.put("currency", currency);
		orderRequest.put("receipt", receipt);

		Order razorpayOrder;
		try {
			razorpayOrder = razorpayClient.orders.create(orderRequest);
		} catch (Exception e) {
			log.error("Razorpay order create failed", e);
			throw new RuntimeException("Payment order creation failed: " + e.getMessage());
		}

		String razorpayOrderId = String.valueOf(razorpayOrder.get("id"));

		Payment payment = Payment.builder()
			.orderId(request.getOrderId())
			.userId(userId)
			.amount(request.getAmount())
			.currency(currency)
			.status(PaymentStatus.INITIATED)
			.razorpayOrderId(razorpayOrderId)
			.idempotencyKey(idempotencyKey)
			.build();

		payment = paymentRepository.save(payment);

		Transaction tx = Transaction.builder()
			.payment(payment)
			.type("CREATE")
			.amount(request.getAmount())
			.status("CREATED")
			.externalId(razorpayOrderId)
			.build();
		transactionRepository.save(tx);

		return PaymentOrderResponse.builder()
			.paymentId(payment.getId())
			.orderId(payment.getOrderId())
			.razorpayOrderId(payment.getRazorpayOrderId())
			.razorpayKeyId(razorpayKeyId)
			.amount(payment.getAmount())
			.currency(payment.getCurrency())
			.build();
	}

	@Override
	@Transactional
	public PaymentResponse verifyAndCapture(VerifyPaymentRequest request, Long userId) {
		Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpay_order_id())
			.orElseThrow(() -> new IllegalArgumentException("Payment not found for order id: " + request.getRazorpay_order_id()));

		if (!payment.getUserId().equals(userId)) {
			throw new IllegalArgumentException("Payment does not belong to user");
		}

		if (payment.getStatus() == PaymentStatus.SUCCESS) {
			return mapToResponse(payment);
		}

		JSONObject options = new JSONObject();
		options.put("razorpay_order_id", request.getRazorpay_order_id());
		options.put("razorpay_payment_id", request.getRazorpay_payment_id());
		options.put("razorpay_signature", request.getRazorpay_signature());

		if (razorpayKeySecret == null || razorpayKeySecret.isBlank()) {
			throw new IllegalStateException("Razorpay secret not configured for signature verification");
		}

		boolean valid;
		try {
			valid = Utils.verifyPaymentSignature(options, razorpayKeySecret);
		} catch (RazorpayException e) {
			log.error("Razorpay signature verification failed", e);
			throw new RuntimeException("Payment verification failed: " + e.getMessage());
		}
		if (!valid) {
			payment.setStatus(PaymentStatus.FAILED);
			paymentRepository.save(payment);
			throw new IllegalArgumentException("Invalid payment signature");
		}

		payment.setRazorpayPaymentId(request.getRazorpay_payment_id());
		payment.setStatus(PaymentStatus.SUCCESS);
		paymentRepository.save(payment);

		Transaction tx = Transaction.builder()
			.payment(payment)
			.type("CAPTURE")
			.amount(payment.getAmount())
			.status("SUCCESS")
			.externalId(request.getRazorpay_payment_id())
			.build();
		transactionRepository.save(tx);

		return mapToResponse(payment);
	}

	@Override
	public PaymentResponse getByOrderId(String orderId, Long userId) {
		Payment payment = paymentRepository.findByOrderIdOrderByCreatedAtDesc(orderId).stream()
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Payment not found for order: " + orderId));
		if (!payment.getUserId().equals(userId)) {
			throw new IllegalArgumentException("Payment does not belong to user");
		}
		return mapToResponse(payment);
	}

	private PaymentResponse mapToResponse(Payment p) {
		return PaymentResponse.builder()
			.id(p.getId())
			.orderId(p.getOrderId())
			.userId(p.getUserId())
			.amount(p.getAmount())
			.currency(p.getCurrency())
			.status(p.getStatus())
			.razorpayOrderId(p.getRazorpayOrderId())
			.razorpayPaymentId(p.getRazorpayPaymentId())
			.createdAt(p.getCreatedAt())
			.updatedAt(p.getUpdatedAt())
			.build();
	}
}
