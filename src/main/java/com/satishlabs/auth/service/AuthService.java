package com.satishlabs.auth.service;

import com.satishlabs.auth.dto.request.LoginRequest;
import com.satishlabs.auth.dto.request.RegisterRequest;
import com.satishlabs.auth.dto.response.AuthResponse;

public interface AuthService {
	AuthResponse login(LoginRequest request);
	void register(RegisterRequest request);
}
