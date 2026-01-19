package com.satishlabs.auth.dto.mapper;

import com.satishlabs.auth.dto.response.UserProfileResponse;
import com.satishlabs.auth.entity.User;

public class UserMapper {

	public static UserProfileResponse toProfileResponse(User user) {

	    return UserProfileResponse.builder()
	            .email(user.getEmail())
	            .name(user.getName())
	            .mobileNumber(user.getMobileNumber())
	            .dob(user.getDob())
	            .age(user.getAge())
	            .gender(user.getGender())
	            .country(user.getCountry())
	            .location(user.getLocation())
	            .profilePic(user.getProfilePic())
	            .createdAt(user.getCreatedAt())
	            .updatedAt(user.getUpdatedAt())
	            .build();
	}

}
