package com.satishlabs.auth.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

/**
 * Principal used when request comes via API Gateway with X-User-Id header (no JWT in service).
 */
public class XUserIdPrincipal implements Authentication {

	private final Long userId;

	public XUserIdPrincipal(Long userId) {
		this.userId = userId;
	}

	public Long getUserId() {
		return userId;
	}

	@Override
	public String getName() {
		return "user-" + userId;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getDetails() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return this;
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) {
		// no-op
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}
}
