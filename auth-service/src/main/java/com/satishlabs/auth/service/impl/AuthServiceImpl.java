package com.satishlabs.auth.service.impl;

import com.satishlabs.auth.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.satishlabs.auth.dto.request.LoginRequest;
import com.satishlabs.auth.dto.request.RegisterRequest;
import com.satishlabs.auth.dto.response.AuthResponse;
import com.satishlabs.auth.entity.PasswordResetToken;
import com.satishlabs.auth.entity.Role;
import com.satishlabs.auth.entity.User;
import com.satishlabs.auth.exception.DuplicateResourceException;
import com.satishlabs.auth.exception.UnauthorizedException;
import com.satishlabs.auth.repository.PasswordResetTokenRepository;
import com.satishlabs.auth.repository.UserRepository;
import com.satishlabs.auth.security.JwtUtil;
import com.satishlabs.auth.service.AuthService;
import com.satishlabs.auth.service.OtpService;
import com.satishlabs.auth.util.AuthProvider;

import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	private final UserRepository userRepository;
	private final PasswordResetTokenRepository passwordResetTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private final OtpService otpService;
	private final JwtUtil jwtUtil;

	@Value("${app.frontend.url:http://localhost:3000}")
	private String frontendUrl;

	private static final SecureRandom RANDOM = new SecureRandom();
	private static final int RESET_TOKEN_VALID_HOURS = 1;
	
	/** Gmail-only: allow only *@gmail.com for registration. */
	private static final java.util.regex.Pattern GMAIL_PATTERN =
			java.util.regex.Pattern.compile("^[a-zA-Z0-9._%+-]+@gmail\\.com$", java.util.regex.Pattern.CASE_INSENSITIVE);

	@Override
	public AuthResponse register(RegisterRequest request) {
		String email = request.getEmail() != null ? request.getEmail().trim() : "";
		if (!GMAIL_PATTERN.matcher(email).matches()) {
			throw new IllegalArgumentException("Only Gmail addresses are allowed for registration (e.g. you@gmail.com).");
		}
		if (userRepository.existsByEmail(email)) {
			throw new DuplicateResourceException("Email already registered");
		}
		if (request.getMobileNumber() != null && !request.getMobileNumber().isBlank()
				&& userRepository.existsByMobileNumber(request.getMobileNumber().trim())) {
			throw new DuplicateResourceException("Mobile number already registered");
		}
		String firstName = request.getFirstName() != null ? request.getFirstName().trim() : "";
		String lastName = request.getLastName() != null ? request.getLastName().trim() : "";
		String name = (firstName + " " + lastName).trim();
		if (name.isEmpty()) name = request.getEmail();

		User user = User.builder()
				.email(email)
				.password(passwordEncoder.encode(request.getPassword()))
				.firstName(firstName)
				.lastName(lastName)
				.name(name)
				.mobileNumber(request.getMobileNumber() != null ? request.getMobileNumber().trim() : null)
				.dob(request.getDob())
				.gender(request.getGender())
				.country(request.getCountry() != null ? request.getCountry().trim() : null)
				.location(request.getLocation() != null ? request.getLocation().trim() : null)
				.role(Role.USER)
				.provider(AuthProvider.LOCAL)
				.enabled(true)
				.build();

		userRepository.save(user);
	    
	    // Send welcome email
	    try {
	        emailService.sendWelcomeEmail(user.getEmail(), user.getName());
	        emailService.sendRegistrationConfirmation(user.getEmail(), user.getName());
	    } catch (Exception e) {
	        // Log but don't fail registration if email fails
	        System.err.println("Failed to send welcome email: " + e.getMessage());
	    }
	    
	    String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole() != null ? user.getRole().name() : "USER");
	    return AuthResponse.builder()
	            .accessToken(token)
	            .userId(user.getId())
	            .email(user.getEmail())
	            .role(user.getRole())
	            .build();
	}

	
	@Override
	public AuthResponse login(LoginRequest request) {
		String username = request.getUsername() != null ? request.getUsername().trim() : "";
		User user = userRepository.findByEmailOrMobileNumber(username)
				.orElseThrow(() -> new UnauthorizedException("Invalid email/phone or password"));

		if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new UnauthorizedException("Invalid email/phone or password");
		}
		if (!user.isEnabled()) {
			throw new UnauthorizedException("Account is disabled");
		}

		String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole() != null ? user.getRole().name() : "USER");
		return AuthResponse.builder()
				.accessToken(token)
				.userId(user.getId())
				.email(user.getEmail())
				.role(user.getRole())
				.build();
	}

	@Override
	public void sendOtp(String emailOrPhone) {
		String input = emailOrPhone != null ? emailOrPhone.trim() : "";
		if (input.isEmpty()) {
			throw new IllegalArgumentException("Email or phone is required");
		}
		// Ensure user exists for this email/phone
		userRepository.findByEmailOrMobileNumber(input)
				.orElseThrow(() -> new UnauthorizedException("No account found with this email or phone number"));
		otpService.sendOtp(input);
	}

	@Override
	public AuthResponse verifyOtp(String emailOrPhone, String otp) {
		String verifiedKey = otpService.verifyAndConsume(emailOrPhone, otp);
		User user = userRepository.findByEmailOrMobileNumber(verifiedKey)
				.orElseThrow(() -> new UnauthorizedException("User not found"));
		if (!user.isEnabled()) {
			throw new UnauthorizedException("Account is disabled");
		}
		String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole() != null ? user.getRole().name() : "USER");
		return AuthResponse.builder()
				.accessToken(token)
				.userId(user.getId())
				.email(user.getEmail())
				.role(user.getRole())
				.build();
	}

	@Override
	public void forgotPassword(String email) {
		String em = email != null ? email.trim().toLowerCase() : "";
		if (!GMAIL_PATTERN.matcher(em).matches()) {
			throw new IllegalArgumentException("Only Gmail addresses are allowed for password reset.");
		}
		User user = userRepository.findByEmailIgnoreCase(em)
				.orElse(null);
		// Do not reveal whether email exists
		if (user == null) return;
		// Remove any existing tokens for this user
		passwordResetTokenRepository.deleteByUserId(user.getId());
		String token = generateResetToken();
		Instant now = Instant.now();
		Instant expiresAt = now.plusSeconds(RESET_TOKEN_VALID_HOURS * 3600L);
		passwordResetTokenRepository.save(PasswordResetToken.builder()
				.userId(user.getId())
				.token(token)
				.expiresAt(expiresAt)
				.createdAt(now)
				.build());
		String resetLink = frontendUrl + "/auth/reset-password?token=" + token;
		emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
	}

	@Override
	public void resetPassword(String token, String newPassword) {
		if (token == null || token.isBlank() || newPassword == null || newPassword.length() < 6) {
			throw new IllegalArgumentException("Invalid token or password (min 6 characters).");
		}
		Instant now = Instant.now();
		PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenAndExpiresAtAfter(token.trim(), now)
				.orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset link. Please request a new one."));
		User user = userRepository.findById(resetToken.getUserId())
				.orElseThrow(() -> new IllegalArgumentException("User not found."));
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
		passwordResetTokenRepository.delete(resetToken);
	}

	private static String generateResetToken() {
		byte[] bytes = new byte[32];
		RANDOM.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}
}
