package com.satishlabs.auth.dto.response;

import com.satishlabs.auth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
	private String accessToken;
	private Long userId;
	private String email;
	private Role role;
	/** When true, client must call POST /auth/verify-2fa with twoFactorTempToken and otp to get accessToken. */
	private Boolean requiresTwoFactor;
	/** Short-lived token to use in verify-2fa when requiresTwoFactor is true. */
	private String twoFactorTempToken;
}
