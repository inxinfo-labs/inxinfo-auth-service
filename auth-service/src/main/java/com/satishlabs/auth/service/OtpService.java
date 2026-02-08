package com.satishlabs.auth.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OtpService {

	private static final int OTP_LENGTH = 6;
	private static final long OTP_VALID_MS = 10 * 60 * 1000; // 10 minutes

	private final ConcurrentHashMap<String, OtpEntry> store = new ConcurrentHashMap<>();
	private final EmailService emailService;

	public OtpService(EmailService emailService) {
		this.emailService = emailService;
	}

	public void sendOtp(String emailOrPhone) {
		String key = (emailOrPhone != null ? emailOrPhone.trim() : "").toLowerCase();
		if (key.isEmpty()) {
			throw new IllegalArgumentException("Email or phone is required");
		}
		String otp = generateOtp();
		store.put(key, new OtpEntry(otp, System.currentTimeMillis()));
		// If it looks like email, send email OTP; otherwise log (phone SMS can be added later)
		if (key.contains("@")) {
			emailService.sendOtpEmail(key, otp);
		} else {
			log.info("OTP for phone {} (SMS not configured): {}", key, otp);
		}
	}

	public String verifyAndConsume(String emailOrPhone, String otp) {
		String key = (emailOrPhone != null ? emailOrPhone.trim() : "").toLowerCase();
		if (key.isEmpty() || otp == null || otp.isBlank()) {
			throw new IllegalArgumentException("Email/phone and OTP are required");
		}
		OtpEntry entry = store.get(key);
		if (entry == null) {
			throw new IllegalArgumentException("OTP not found or expired. Please request a new one.");
		}
		if (System.currentTimeMillis() - entry.createdAt > OTP_VALID_MS) {
			store.remove(key);
			throw new IllegalArgumentException("OTP expired. Please request a new one.");
		}
		if (!entry.otp.equals(otp.trim())) {
			throw new IllegalArgumentException("Invalid OTP.");
		}
		store.remove(key);
		return key;
	}

	private String generateOtp() {
		String digits = "0123456789";
		StringBuilder sb = new StringBuilder(OTP_LENGTH);
		for (int i = 0; i < OTP_LENGTH; i++) {
			sb.append(digits.charAt((int) (Math.random() * digits.length())));
		}
		return sb.toString();
	}

	private static class OtpEntry {
		final String otp;
		final long createdAt;

		OtpEntry(String otp, long createdAt) {
			this.otp = otp;
			this.createdAt = createdAt;
		}
	}
}
