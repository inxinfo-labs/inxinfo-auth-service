package com.satishlabs.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.satishlabs.auth.dto.request.ContactRequest;
import com.satishlabs.auth.dto.response.ApiResponse;
import com.satishlabs.auth.service.EmailService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Public contact form. Sends message to admin (satish.prasad@inxinfo.com by default).
 */
@RestController
@RequestMapping("/contact")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "https://inxinfo-user-portal-1.onrender.com", "https://www.inxinfo.com", "https://inxinfo.com"})
public class ContactController {

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> sendMessage(@Valid @RequestBody ContactRequest request) {
        emailService.sendContactToAdmin(
            request.getName(),
            request.getEmail(),
            request.getSubject(),
            request.getMessage()
        );
        return ResponseEntity.ok(new ApiResponse<>(200, "Message sent. We will reply to you shortly.", null));
    }
}
