package com.satishlabs.auth.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.satishlabs.auth.entity.Gender;
import com.satishlabs.auth.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {

    private String email;
    private String name;
    private String mobileNumber;
    private LocalDate dob;
    private Integer age;
    private Gender gender;
    private String country;
    private String location;
    private String profilePic;
    private Role role;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}



