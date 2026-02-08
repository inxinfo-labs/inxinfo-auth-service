package com.satishlabs.pandit.dto.request;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/** Admin approves an existing user (by userId) as Pandit. Name, email, mobile come from auth user. */
@Data
public class PanditFromUserRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    private String address;
    private String city;
    private String state;
    private String pincode;
    private String bio;

    @NotNull(message = "Experience years is required")
    @Positive(message = "Experience must be positive")
    private Integer experienceYears;

    @NotNull(message = "Hourly rate is required")
    @DecimalMin(value = "0", message = "Hourly rate must be >= 0")
    private BigDecimal hourlyRate;

    private List<String> specializations;
}
