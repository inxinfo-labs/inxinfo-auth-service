package com.satishlabs.auth.dto.mapper;

import com.satishlabs.auth.dto.response.UserProfileResponse;
import com.satishlabs.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
		componentModel = "spring",
		unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

	UserProfileResponse toProfileResponse(User user);
}
