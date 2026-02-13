package com.satishlabs.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.satishlabs.auth.constants.ErrorCodes;
import com.satishlabs.auth.dto.request.ContactRequest;
import com.satishlabs.auth.dto.response.SuccessResponse;
import com.satishlabs.auth.exception.BusinessException;
import com.satishlabs.auth.service.EmailService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Public contact form. Sends message to admin (satish.prasad@inxinfo.com by default).
 */
@RestController
@RequestMapping("/contact")
@RequiredArgsConstructor
public class ContactController {

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> sendMessage(@Valid @RequestBody ContactRequest request) {
        try {
            emailService.sendContactToAdmin(
                request.getName(),
                request.getEmail(),
                request.getSubject(),
                request.getMessage()
            );
            return ResponseEntity.ok(SuccessResponse.of(null));
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CONTACT_503_001,
                e.getMessage() != null ? e.getMessage() : ErrorCodes.CONTACT_503_001.getDefaultMessage());
        }
    }
}
