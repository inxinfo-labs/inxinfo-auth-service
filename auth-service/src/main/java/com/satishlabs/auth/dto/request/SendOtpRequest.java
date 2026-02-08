package com.satishlabs.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendOtpRequest {

	@NotBlank(message = "Email or phone number is required")
	private String emailOrPhone;
}
