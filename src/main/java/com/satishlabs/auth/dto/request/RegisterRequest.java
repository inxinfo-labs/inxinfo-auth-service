package com.satishlabs.auth.dto.request;

import java.time.LocalDate;

import com.satishlabs.auth.entity.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    private LocalDate dob;
    private Gender gender;
}
