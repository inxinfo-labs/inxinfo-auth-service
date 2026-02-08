package com.satishlabs.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

	/** Email, username, or phone number */
	@NotBlank(message = "Email, username or phone is required")
	private String username;

	@NotBlank(message = "Password is required")
	private String password;
}
