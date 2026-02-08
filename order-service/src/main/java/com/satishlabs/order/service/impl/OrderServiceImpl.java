package com.satishlabs.order.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.satishlabs.order.client.AuthClient;
import com.satishlabs.order.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.satishlabs.auth.exception.ResourceNotFoundException;
import com.satishlabs.order.dto.request.CreateOrderRequest;
import com.satishlabs.order.dto.request.OrderItemRequest;
import com.satishlabs.order.dto.request.ProductItemRequest;
import com.satishlabs.order.dto.response.OrderItemResponse;
import com.satishlabs.order.dto.response.OrderResponse;
import com.satishlabs.order.dto.response.ProductItemResponse;
import com.satishlabs.order.entity.Item;
import com.satishlabs.order.entity.Order;
import com.satishlabs.order.entity.OrderItem;
import com.satishlabs.order.entity.OrderProductItem;
import com.satishlabs.order.entity.OrderStatus;
import com.satishlabs.order.entity.PaymentStatus;
import com.satishlabs.order.repository.ItemRepository;
import com.satishlabs.order.repository.OrderRepository;
import com.satishlabs.order.saga.SagaOrchestrator;
import com.satishlabs.puja.entity.PujaType;
import com.satishlabs.puja.repository.PujaTypeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    @Qualifier("orderAuthClient") private final AuthClient authClient;
    private final PujaTypeRepository pujaTypeRepository;
    private final ItemRepository itemRepository;
    private final SagaOrchestrator sagaOrchestrator;

    @Override
    public OrderResponse createOrder(CreateOrderRequest request, Long userId, String authorizationHeader) {
        Order order = Order.builder()
                .userId(userId)
                .shippingAddress(request.getShippingAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .contactPhone(request.getContactPhone())
                .notes(request.getNotes())
                .status(OrderStatus.PENDING)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        if (request.getItems() != null) {
            for (OrderItemRequest itemRequest : request.getItems()) {
                PujaType pujaType = pujaTypeRepository.findById(itemRequest.getPujaTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Puja type not found with id: " + itemRequest.getPujaTypeId()));

                BigDecimal subtotal = pujaType.getPrice()
                        .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

                OrderItem orderItem = OrderItem.builder()
                        .order(order)
                        .pujaType(pujaType)
                        .quantity(itemRequest.getQuantity())
                        .unitPrice(pujaType.getPrice())
                        .subtotal(subtotal)
                        .build();

                order.getOrderItems().add(orderItem);
                totalAmount = totalAmount.add(subtotal);
            }
        }

        if (request.getProductItems() != null) {
            for (ProductItemRequest pi : request.getProductItems()) {
                Item item = itemRepository.findById(pi.getItemId())
                        .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + pi.getItemId()));
                if (!item.isActive()) {
                    throw new ResourceNotFoundException("Item is not available: " + item.getName());
                }
                BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(pi.getQuantity()));
                OrderProductItem opi = OrderProductItem.builder()
                        .order(order)
                        .item(item)
                        .quantity(pi.getQuantity())
                        .unitPrice(item.getPrice())
                        .subtotal(subtotal)
                        .build();
                order.getProductItems().add(opi);
                totalAmount = totalAmount.add(subtotal);
            }
        }

        if (order.getOrderItems().isEmpty() && order.getProductItems().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one puja service or product item");
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        if (request.getPanditId() != null) {
            try {
                sagaOrchestrator.execute(request, userId, savedOrder);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return mapToOrderResponse(orderRepository.findById(savedOrder.getId()).orElseThrow(), authorizationHeader);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(Long userId, String authorizationHeader) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(o -> mapToOrderResponse(o, authorizationHeader))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, Long userId, String authorizationHeader) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Order not found");
        }

        return mapToOrderResponse(order, authorizationHeader);
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        try {
            order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        Order updatedOrder = orderRepository.save(order);
        return mapToOrderResponse(updatedOrder, null);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderNumber(String orderNumber, Long userId, String authorizationHeader) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with order number: " + orderNumber));

        if (!order.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Order not found");
        }

        return mapToOrderResponse(order, authorizationHeader);
    }

    @Override
    public OrderResponse confirmPayment(Long orderId, String paymentId, Long userId, String authorizationHeader) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        if (!order.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Order not found");
        }
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            return mapToOrderResponse(order, authorizationHeader);
        }
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setPaymentId(paymentId != null ? paymentId : "MOCK-" + orderId);
        order.setStatus(OrderStatus.CONFIRMED);
        return mapToOrderResponse(orderRepository.save(order), authorizationHeader);
    }

    private OrderResponse mapToOrderResponse(Order order, String authorizationHeader) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());
        List<ProductItemResponse> productItemResponses = order.getProductItems().stream()
                .map(this::mapToProductItemResponse)
                .collect(Collectors.toList());

        String userName = authClient.getUserById(order.getUserId(), authorizationHeader)
                .map(u -> u.getName() != null ? u.getName() : u.getEmail())
                .orElse("");

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .userName(userName)
                .orderItems(itemResponses)
                .productItems(productItemResponses)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .paymentId(order.getPaymentId())
                .shippingAddress(order.getShippingAddress())
                .city(order.getCity())
                .state(order.getState())
                .pincode(order.getPincode())
                .contactPhone(order.getContactPhone())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private OrderItemResponse mapToOrderItemResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .pujaTypeId(orderItem.getPujaType().getId())
                .pujaTypeName(orderItem.getPujaType().getName())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .subtotal(orderItem.getSubtotal())
                .build();
    }

    private ProductItemResponse mapToProductItemResponse(OrderProductItem opi) {
        return ProductItemResponse.builder()
                .id(opi.getId())
                .itemId(opi.getItem().getId())
                .itemName(opi.getItem().getName())
                .quantity(opi.getQuantity())
                .unitPrice(opi.getUnitPrice())
                .subtotal(opi.getSubtotal())
                .build();
    }
}
