package com.satishlabs.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.satishlabs.auth.constants.AuthConstants;
import com.satishlabs.auth.dto.request.ForgotPasswordRequest;
import com.satishlabs.auth.dto.request.LoginRequest;
import com.satishlabs.auth.dto.request.RegisterRequest;
import com.satishlabs.auth.dto.request.ResetPasswordRequest;
import com.satishlabs.auth.dto.request.SendOtpRequest;
import com.satishlabs.auth.dto.request.VerifyOtpRequest;
import com.satishlabs.auth.dto.response.ApiResponse;
import com.satishlabs.auth.dto.response.AuthResponse;
import com.satishlabs.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

	private final AuthService authService;

	/** Health check so clients can verify auth module is mounted (e.g. under /api/auth). */
	@GetMapping(value = "/health", produces = "application/json")
	public ResponseEntity<java.util.Map<String, String>> health() {
		return ResponseEntity.ok(java.util.Map.of("status", "up", "module", "auth"));
	}

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.ok(authService.register(request));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@PostMapping(value = "/send-otp", consumes = "application/json", produces = "application/json")
	public ResponseEntity<ApiResponse<Void>> sendOtp(@Valid @RequestBody SendOtpRequest request) {
		authService.sendOtp(request.getEmailOrPhone());
		return ResponseEntity.ok(new ApiResponse<>(AuthConstants.CODE_OTP_SENT, AuthConstants.MSG_OTP_SENT, null));
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<AuthResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
		return ResponseEntity.ok(authService.verifyOtp(request.getEmailOrPhone(), request.getOtp()));
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout() {
		return ResponseEntity.ok("Logout successful (client-side token discard)");
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
		authService.forgotPassword(request.getEmail());
		return ResponseEntity.ok(new ApiResponse<>(AuthConstants.CODE_SUCCESS, "If an account exists for this email, a reset link has been sent.", null));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
		authService.resetPassword(request.getToken(), request.getNewPassword());
		return ResponseEntity.ok(new ApiResponse<>(AuthConstants.CODE_SUCCESS, "Password updated. You can now sign in.", null));
	}
}
