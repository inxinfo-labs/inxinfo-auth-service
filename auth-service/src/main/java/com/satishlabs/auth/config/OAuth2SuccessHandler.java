package com.satishlabs.auth.config;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.satishlabs.auth.entity.Role;
import com.satishlabs.auth.entity.User;
import com.satishlabs.auth.repository.UserRepository;
import com.satishlabs.auth.security.JwtUtil;
import com.satishlabs.auth.util.AuthProvider;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        OAuth2AuthenticationToken authToken =
                (OAuth2AuthenticationToken) authentication;

        OAuth2User oAuth2User = authToken.getPrincipal();

        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");

        // 🔹 Create or fetch user
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(email)
                                .name(name)
                                .profilePic(picture)
                                .provider(AuthProvider.GOOGLE)
                                .role(Role.USER)
                                .enabled(true)
                                .build()
                ));

        // 🔹 Generate JWT (include userId for gateway)
        String jwt = jwtUtil.generateToken(user.getEmail(), user.getId());

        // 🔹 Return JWT (JSON)
        response.setContentType("application/json");
        response.getWriter().write("""
            {
              "token": "%s",
              "email": "%s",
              "provider": "GOOGLE"
            }
            """.formatted(jwt, email));
    }
}
