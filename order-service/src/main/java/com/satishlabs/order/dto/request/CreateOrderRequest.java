package com.satishlabs.order.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateOrderRequest {
    /** Puja services (at least one of items or productItems recommended) */
    @Valid
    private List<OrderItemRequest> items;

    /** Additional products/items (e.g. ritual items) */
    @Valid
    private List<ProductItemRequest> productItems;

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    @NotBlank(message = "City is required")
    private String city;

    private String state;

    @NotBlank(message = "Pincode is required")
    private String pincode;

    @NotBlank(message = "Contact phone is required")
    private String contactPhone;

    private String notes;

    /** Optional: for Saga, reserve this pandit for the order */
    private Long panditId;
}
