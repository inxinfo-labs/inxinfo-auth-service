package com.satishlabs.auth.service;

import com.satishlabs.auth.dto.request.LoginRequest;
import com.satishlabs.auth.dto.request.RegisterRequest;
import com.satishlabs.auth.dto.response.AuthResponse;

public interface AuthService {
	AuthResponse login(LoginRequest request);
	AuthResponse register(RegisterRequest request);
	void sendOtp(String emailOrPhone);
	AuthResponse verifyOtp(String emailOrPhone, String otp);
	void forgotPassword(String email);
	void resetPassword(String token, String newPassword);
}
