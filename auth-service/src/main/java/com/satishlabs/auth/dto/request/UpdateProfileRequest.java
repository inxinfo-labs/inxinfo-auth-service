package com.satishlabs.auth.dto.request;

import java.time.LocalDate;

import com.satishlabs.auth.entity.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

	@NotBlank(message = "First name is required")
	@Size(max = 100)
	private String firstName;

	@NotBlank(message = "Last name is required")
	@Size(max = 100)
	private String lastName;

	private String mobileNumber;
	private LocalDate dob;
	private Gender gender;
	private String country;
	private String location;
}
