package com.satishlabs.auth.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.satishlabs.auth.entity.Role;
import com.satishlabs.auth.entity.User;
import com.satishlabs.auth.repository.UserRepository;
import com.satishlabs.auth.util.AuthProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Seeds default admin user on startup if not present.
 * Admin: admin@inxinfo.com / Admin@123 (change password after first login).
 */
@Component
@Order(1)
@Slf4j
@Profile("!test")
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private static final String ADMIN_EMAIL = "admin@inxinfo.com";
    private static final String ADMIN_DEFAULT_PASSWORD = "Admin@123";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByEmail(ADMIN_EMAIL).isEmpty()) {
            User admin = User.builder()
                    .email(ADMIN_EMAIL)
                    .password(passwordEncoder.encode(ADMIN_DEFAULT_PASSWORD))
                    .firstName("Admin")
                    .lastName("User")
                    .name("Admin User")
                    .role(Role.ADMIN)
                    .provider(AuthProvider.LOCAL)
                    .enabled(true)
                    .build();
            userRepository.save(admin);
            log.info("Default admin user created: {} (password: {})", ADMIN_EMAIL, ADMIN_DEFAULT_PASSWORD);
        }
    }
}
