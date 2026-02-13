package com.satishlabs.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyTwoFactorRequest {
    @NotBlank(message = "Temp token is required")
    private String twoFactorTempToken;
    @NotBlank(message = "OTP is required")
    private String otp;
}
