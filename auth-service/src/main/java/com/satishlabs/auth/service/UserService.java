package com.satishlabs.auth.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.satishlabs.auth.dto.request.UpdatePasswordRequest;
import com.satishlabs.auth.dto.request.UpdateProfileRequest;
import com.satishlabs.auth.dto.response.UserProfileResponse;
import com.satishlabs.auth.entity.Role;

public interface UserService {

    UserProfileResponse getProfile();

    /** For other services (order, pandit, puja) to resolve userId → display name. Requires valid JWT. */
    UserProfileResponse getProfileById(Long id);

    void updateProfile(UpdateProfileRequest request);

    void updatePassword(UpdatePasswordRequest request);

    void uploadProfilePic(MultipartFile file);

    /** Admin: list all users (for approve-as-pandit, etc.). */
    List<UserProfileResponse> getAllUsers();

    /** Admin: change user role (e.g. USER, ADMIN). */
    void updateUserRole(Long id, Role role);

    /** Admin: enable or disable user account. */
    void setUserEnabled(Long id, boolean enabled);

    /** Send OTP to current user's email to enable 2FA. Then call confirmTwoFactor(otp). */
    void sendTwoFactorSetupOtp();

    /** Verify OTP and enable 2FA for current user. Call after sendTwoFactorSetupOtp. */
    void confirmTwoFactor(String otp);

    /** Disable 2FA for current user. Requires password. */
    void disableTwoFactor(String password);
}
