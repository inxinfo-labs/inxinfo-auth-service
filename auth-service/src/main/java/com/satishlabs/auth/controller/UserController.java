package com.satishlabs.auth.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.satishlabs.auth.config.AppProperties;
import com.satishlabs.auth.dto.request.ConfirmTwoFactorRequest;
import com.satishlabs.auth.dto.request.DisableTwoFactorRequest;
import com.satishlabs.auth.dto.request.UpdatePasswordRequest;
import com.satishlabs.auth.dto.request.UpdateProfileRequest;
import com.satishlabs.auth.dto.response.SuccessResponse;
import com.satishlabs.auth.dto.response.UserProfileResponse;
import com.satishlabs.auth.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AppProperties appProperties;

    // ================= GET PROFILE =================
    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<UserProfileResponse>> getProfile() {
        return ResponseEntity.ok(SuccessResponse.of(userService.getProfile()));
    }

    /** For other services (order, pandit, puja) to resolve userId → display info. Requires valid JWT. */
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<UserProfileResponse>> getProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(SuccessResponse.of(userService.getProfileById(id)));
    }

    // ================= UPDATE PROFILE =================
    @PutMapping("/profile")
    public ResponseEntity<SuccessResponse<Void>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {

        userService.updateProfile(request);
        return ResponseEntity.ok(SuccessResponse.of(null));
    }

    // ================= UPDATE PASSWORD =================
    @PutMapping("/password")
    public ResponseEntity<SuccessResponse<Void>> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request) {

        userService.updatePassword(request);
        return ResponseEntity.ok(SuccessResponse.of(null));
    }

    // ================= UPLOAD PROFILE PIC =================
    @PostMapping("/profile-pic")
    public ResponseEntity<SuccessResponse<Void>> uploadProfilePic(
            @RequestParam("file") MultipartFile file) {

        userService.uploadProfilePic(file);
        return ResponseEntity.ok(SuccessResponse.of(null));
    }

    // ================= GET PROFILE PIC =================
    @GetMapping("/profile-pic")
    public ResponseEntity<Resource> getProfilePic() throws IOException {

        UserProfileResponse profile = userService.getProfile();

        if (profile.getProfilePic() == null) {
            return ResponseEntity.notFound().build();
        }

        Path path = Paths.get(appProperties.getUpload().getProfilePicPath()).resolve(profile.getProfilePic());
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    // ================= TWO-FACTOR AUTHENTICATION =================
    @PostMapping("/2fa/send-setup-otp")
    public ResponseEntity<SuccessResponse<Void>> sendTwoFactorSetupOtp() {
        userService.sendTwoFactorSetupOtp();
        return ResponseEntity.ok(SuccessResponse.of(null));
    }

    @PostMapping("/2fa/confirm")
    public ResponseEntity<SuccessResponse<Void>> confirmTwoFactor(
            @Valid @RequestBody ConfirmTwoFactorRequest request) {
        userService.confirmTwoFactor(request.getOtp());
        return ResponseEntity.ok(SuccessResponse.of(null));
    }

    @PostMapping("/2fa/disable")
    public ResponseEntity<SuccessResponse<Void>> disableTwoFactor(
            @Valid @RequestBody DisableTwoFactorRequest request) {
        userService.disableTwoFactor(request.getPassword());
        return ResponseEntity.ok(SuccessResponse.of(null));
    }

}
