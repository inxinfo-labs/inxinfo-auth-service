package com.satishlabs.puja.client;

import com.satishlabs.auth.dto.response.UserProfileResponse;

import java.util.Optional;

/** Resolves userId → user display info. In distributed mode calls auth-service; in monolith uses local UserRepository. */
public interface AuthClient {

    Optional<UserProfileResponse> getUserById(Long userId, String authorizationHeader);
}
