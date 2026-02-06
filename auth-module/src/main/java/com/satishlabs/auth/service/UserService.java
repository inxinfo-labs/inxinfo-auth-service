package com.satishlabs.auth.service;

import org.springframework.web.multipart.MultipartFile;
import com.satishlabs.auth.dto.request.UpdatePasswordRequest;
import com.satishlabs.auth.dto.request.UpdateProfileRequest;
import com.satishlabs.auth.dto.response.UserProfileResponse;

public interface UserService {

    UserProfileResponse getProfile();

    void updateProfile(UpdateProfileRequest request);

    void updatePassword(UpdatePasswordRequest request);

    void uploadProfilePic(MultipartFile file);
}
