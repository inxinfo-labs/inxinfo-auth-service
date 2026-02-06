package com.satishlabs.auth.service.impl;

import com.satishlabs.auth.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.satishlabs.auth.dto.request.LoginRequest;
import com.satishlabs.auth.dto.request.RegisterRequest;
import com.satishlabs.auth.dto.response.AuthResponse;
import com.satishlabs.auth.entity.Role;
import com.satishlabs.auth.entity.User;
import com.satishlabs.auth.exception.DuplicateResourceException;
import com.satishlabs.auth.repository.UserRepository;
import com.satishlabs.auth.security.JwtUtil;
import com.satishlabs.auth.service.AuthService;
import com.satishlabs.auth.util.AuthProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private final JwtUtil jwtUtil;
	
	@Override
	public AuthResponse register(RegisterRequest request) {
		   if (userRepository.existsByEmail(request.getEmail())) {
		        throw new DuplicateResourceException("Email already registered");
		    }
	    User user = User.builder()
	            .email(request.getEmail())
	            .password(passwordEncoder.encode(request.getPassword()))
	            .name(request.getName())
	            .dob(request.getDob())
	            .gender(request.getGender())
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
	    
	    // Generate token after successful registration
	    String token = jwtUtil.generateToken(user.getEmail());
	    return new AuthResponse(token);
	}

	
	@Override
	public AuthResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("Invalid credentials"));
		
		if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new RuntimeException("Invalid credentials");
		}
		
		String token = jwtUtil.generateToken(user.getEmail());
		return new AuthResponse(token);
	}
	

}
