package com.satishlabs.pandit.dto.request;

import java.math.BigDecimal;
import java.util.List;

import com.satishlabs.pandit.entity.PanditStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PanditRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "Mobile number is required")
    private String mobileNumber;

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

    private String profileImageUrl;

    private List<String> specializations;

    private PanditStatus status = PanditStatus.AVAILABLE;

    private Boolean active = true;
}
