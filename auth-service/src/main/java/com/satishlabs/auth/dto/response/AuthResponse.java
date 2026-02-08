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
}
