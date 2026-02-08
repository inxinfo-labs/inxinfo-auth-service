package com.satishlabs.puja.client;

import com.satishlabs.auth.dto.response.ApiResponse;
import com.satishlabs.auth.dto.response.UserProfileResponse;
import com.satishlabs.auth.dto.mapper.UserMapper;
import com.satishlabs.auth.entity.User;
import com.satishlabs.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component("pujaAuthClient")
@Slf4j
@RequiredArgsConstructor
public class AuthClientImpl implements AuthClient {

    @Value("${auth.service.url:}")
    private String authServiceUrl;

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<UserProfileResponse> getUserById(Long userId, String authorizationHeader) {
        if (userId == null) return Optional.empty();
        if (authServiceUrl != null && !authServiceUrl.isBlank()) {
            return fetchFromAuthService(userId, authorizationHeader);
        }
        return userRepository.findById(userId).map(userMapper::toProfileResponse);
    }

    private Optional<UserProfileResponse> fetchFromAuthService(Long userId, String authorizationHeader) {
        try {
            String url = authServiceUrl.replaceAll("/$", "") + "/user/" + userId;
            HttpHeaders headers = new HttpHeaders();
            if (authorizationHeader != null && !authorizationHeader.isBlank()) {
                headers.set("Authorization", authorizationHeader);
            }
            ResponseEntity<ApiResponse<UserProfileResponse>> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers),
                    new ParameterizedTypeReference<ApiResponse<UserProfileResponse>>() {});
            if (response.getBody() != null && response.getBody().getData() != null) {
                return Optional.of(response.getBody().getData());
            }
        } catch (Exception e) {
            log.warn("Auth service call failed for userId={}: {}", userId, e.getMessage());
        }
        return Optional.empty();
    }
}
