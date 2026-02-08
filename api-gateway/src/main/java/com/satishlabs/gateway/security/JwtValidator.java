package com.satishlabs.gateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Optional;

@Component
public class JwtValidator {

	private static final String USER_ID_CLAIM = "userId";

	@Value("${gateway.jwt.secret:satishlabs-secret-key-satishlabs-secret-key}")
	private String secret;

	private SecretKey key() {
		return Keys.hmacShaKeyFor(secret.getBytes());
	}

	public boolean isValid(String token) {
		try {
			Jwts.parserBuilder()
					.setSigningKey(key())
					.build()
					.parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Optional<Long> extractUserId(String token) {
		try {
			Object userId = Jwts.parserBuilder()
					.setSigningKey(key())
					.build()
					.parseClaimsJws(token)
					.getBody()
					.get(USER_ID_CLAIM);
			if (userId == null) return Optional.empty();
			if (userId instanceof Integer) return Optional.of(((Integer) userId).longValue());
			if (userId instanceof Long) return Optional.of((Long) userId);
			return Optional.of(Long.parseLong(userId.toString()));
		} catch (Exception e) {
			return Optional.empty();
		}
	}
}
