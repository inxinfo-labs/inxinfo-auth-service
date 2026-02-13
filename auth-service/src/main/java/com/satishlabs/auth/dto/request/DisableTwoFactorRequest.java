package com.satishlabs.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DisableTwoFactorRequest {
    @NotBlank(message = "Password is required to disable 2FA")
    private String password;
}
