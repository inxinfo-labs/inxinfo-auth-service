package com.satishlabs.auth.dto.request;

import java.time.LocalDate;

import com.satishlabs.auth.entity.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @NotBlank
    private String name;

    private String mobileNumber;
    private LocalDate dob;     // client sends DOB
    @NotNull(message = "Gender is required")
    private Gender gender;
    private String country;
    private String location;
}

