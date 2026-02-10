package com.satishlabs.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContactRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 200)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    private String email;

    @Size(max = 300)
    private String subject;

    @NotBlank(message = "Message is required")
    @Size(max = 5000)
    private String message;
}
