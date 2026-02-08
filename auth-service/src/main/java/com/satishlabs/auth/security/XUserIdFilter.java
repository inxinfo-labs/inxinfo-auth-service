package com.satishlabs.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * When API Gateway forwards with X-User-Id header, set SecurityContext so controllers can get userId.
 */
public class XUserIdFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
	                                FilterChain filterChain) throws ServletException, IOException {
		String userIdHeader = request.getHeader("X-User-Id");
		if (userIdHeader != null && !userIdHeader.isBlank()) {
			try {
				Long userId = Long.parseLong(userIdHeader.trim());
				SecurityContextHolder.getContext().setAuthentication(new XUserIdPrincipal(userId));
			} catch (NumberFormatException ignored) {
				// ignore invalid header
			}
		}
		filterChain.doFilter(request, response);
	}
}
