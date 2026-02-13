package com.satishlabs.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.satishlabs.auth.dto.request.ForgotPasswordRequest;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import com.satishlabs.auth.dto.request.LoginRequest;
import com.satishlabs.auth.dto.request.RegisterRequest;
import com.satishlabs.auth.dto.request.ResetPasswordRequest;
import com.satishlabs.auth.dto.request.SendOtpRequest;
import com.satishlabs.auth.dto.request.VerifyOtpRequest;
import com.satishlabs.auth.dto.response.AuthResponse;
import com.satishlabs.auth.dto.response.SuccessResponse;
import com.satishlabs.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;

	/** Health check so clients can verify auth module is mounted (e.g. under /api/auth). */
	@GetMapping(value = "/health", produces = "application/json")
	public ResponseEntity<java.util.Map<String, String>> health() {
		return ResponseEntity.ok(java.util.Map.of("status", "up", "module", "auth"));
	}

	@PostMapping("/register")
	@RateLimiter(name = "auth")
	public ResponseEntity<SuccessResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.ok(SuccessResponse.of(authService.register(request)));
	}

	@PostMapping("/login")
	@RateLimiter(name = "auth")
	public ResponseEntity<SuccessResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(SuccessResponse.of(authService.login(request)));
	}

	@PostMapping(value = "/send-otp", consumes = "application/json", produces = "application/json")
	public ResponseEntity<SuccessResponse<Void>> sendOtp(@Valid @RequestBody SendOtpRequest request) {
		authService.sendOtp(request.getEmailOrPhone());
		return ResponseEntity.ok(SuccessResponse.of(null));
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<SuccessResponse<AuthResponse>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
		return ResponseEntity.ok(SuccessResponse.of(authService.verifyOtp(request.getEmailOrPhone(), request.getOtp())));
	}

	@PostMapping("/logout")
	public ResponseEntity<SuccessResponse<String>> logout() {
		return ResponseEntity.ok(SuccessResponse.of("Logout successful (client-side token discard)"));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<SuccessResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
		authService.forgotPassword(request.getEmail());
		return ResponseEntity.ok(SuccessResponse.of(null));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<SuccessResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
		authService.resetPassword(request.getToken(), request.getNewPassword());
		return ResponseEntity.ok(SuccessResponse.of(null));
	}
}
