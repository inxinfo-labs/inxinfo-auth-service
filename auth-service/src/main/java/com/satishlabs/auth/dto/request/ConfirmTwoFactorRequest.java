package com.satishlabs.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfirmTwoFactorRequest {
    @NotBlank(message = "OTP is required")
    private String otp;
}
