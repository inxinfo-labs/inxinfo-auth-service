package com.satishlabs.order.saga;

import com.satishlabs.order.dto.request.CreateOrderRequest;
import com.satishlabs.order.dto.response.OrderResponse;
import com.satishlabs.order.entity.Order;
import com.satishlabs.order.entity.OrderStatus;
import com.satishlabs.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Saga: Create Order → Reserve Pandit → Confirm Puja → Send Notification.
 * Compensation: release pandit, set order FAILED.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SagaOrchestrator {

	private final OrderRepository orderRepository;
	private final RestTemplate restTemplate;

	@Value("${order.saga.pandit-url:http://localhost:8083}")
	private String panditUrl;

	@Value("${order.saga.notify-url:http://localhost:8085}")
	private String notifyUrl;

	@Value("${order.saga.enabled:false}")
	private boolean sagaEnabled;

	public OrderResponse execute(CreateOrderRequest request, Long userId, Order savedOrder) {
		if (!sagaEnabled || request.getPanditId() == null) {
			return null; // caller will return order as-is
		}
		Long orderId = savedOrder.getId();
		Long panditId = request.getPanditId();
		boolean reserved = false;
		try {
			// Step 2: Reserve Pandit
			Map<String, Long> reserveBody = Map.of("orderId", orderId, "panditId", panditId);
			ResponseEntity<Map> reserveRes = restTemplate.exchange(
					panditUrl + "/pandit/reserve",
					HttpMethod.POST,
					new HttpEntity<>(reserveBody, jsonHeaders()),
					Map.class
			);
			if (!reserveRes.getStatusCode().is2xxSuccessful()) {
				throw new RuntimeException("Pandit reserve failed");
			}
			reserved = true;
			log.info("Saga: pandit reserved for order {}", orderId);

			// Step 3: Confirm Puja (update order status)
			savedOrder.setStatus(OrderStatus.CONFIRMED);
			orderRepository.save(savedOrder);

			// Step 4: Send Notification
			String emailBody = "Order " + savedOrder.getOrderNumber() + " confirmed. Total: " + savedOrder.getTotalAmount();
			Map<String, String> notifyBody = Map.of(
					"to", "customer@example.com",
					"subject", "Order Confirmed: " + savedOrder.getOrderNumber(),
					"body", emailBody
			);
			restTemplate.exchange(
					notifyUrl + "/notify/email",
					HttpMethod.POST,
					new HttpEntity<>(notifyBody, jsonHeaders()),
					Map.class
			);
			log.info("Saga: notification sent for order {}", orderId);
			return null;
		} catch (Exception e) {
			log.warn("Saga failed for order {}: {}", orderId, e.getMessage());
			// Compensation
			if (reserved) {
				try {
					restTemplate.exchange(
							panditUrl + "/pandit/release",
							HttpMethod.POST,
							new HttpEntity<>(Map.of("orderId", orderId), jsonHeaders()),
							Map.class
					);
					log.info("Saga: compensation - pandit released for order {}", orderId);
				} catch (Exception ex) {
					log.error("Saga compensation (release) failed: {}", ex.getMessage());
				}
			}
			savedOrder.setStatus(OrderStatus.CANCELLED);
			orderRepository.save(savedOrder);
			throw new RuntimeException("Order saga failed: " + e.getMessage());
		}
	}

	private HttpHeaders jsonHeaders() {
		HttpHeaders h = new HttpHeaders();
		h.setContentType(MediaType.APPLICATION_JSON);
		return h;
	}
}
