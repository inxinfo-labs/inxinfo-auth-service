package com.satishlabs.payment.repository;

import com.satishlabs.payment.entity.Payment;
import com.satishlabs.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	Optional<Payment> findByIdempotencyKey(String idempotencyKey);

	List<Payment> findByOrderIdOrderByCreatedAtDesc(String orderId);

	List<Payment> findByUserIdOrderByCreatedAtDesc(Long userId);

	Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
}
