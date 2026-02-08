package com.satishlabs.auth.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.satishlabs.auth.constants.AuthConstants;
import com.satishlabs.auth.dto.request.UpdatePasswordRequest;
import com.satishlabs.auth.dto.request.UpdateProfileRequest;
import com.satishlabs.auth.dto.response.ApiResponse;
import com.satishlabs.auth.dto.response.UserProfileResponse;
import com.satishlabs.auth.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    @Value("${app.upload.profile-pic-path:uploads/profile-pics}")
    private String uploadDir;

    // ================= GET PROFILE =================
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile() {
        return ResponseEntity.ok(
                new ApiResponse<>(AuthConstants.CODE_PROFILE_FETCHED, AuthConstants.MSG_PROFILE_FETCHED, userService.getProfile())
        );
    }

    /** For other services (order, pandit, puja) to resolve userId → display info. Requires valid JWT. */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(
                new ApiResponse<>(AuthConstants.CODE_PROFILE_FETCHED, AuthConstants.MSG_PROFILE_FETCHED, userService.getProfileById(id))
        );
    }

    // ================= UPDATE PROFILE =================
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {

        userService.updateProfile(request);
        return ResponseEntity.ok(new ApiResponse<>(AuthConstants.CODE_PROFILE_UPDATED, AuthConstants.MSG_PROFILE_UPDATED, null));
    }

    // ================= UPDATE PASSWORD =================
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request) {

        userService.updatePassword(request);
        return ResponseEntity.ok(new ApiResponse<>(AuthConstants.CODE_PASSWORD_UPDATED, AuthConstants.MSG_PASSWORD_UPDATED, null));
    }

    // ================= UPLOAD PROFILE PIC =================
    @PostMapping("/profile-pic")
    public ResponseEntity<ApiResponse<Void>> uploadProfilePic(
            @RequestParam("file") MultipartFile file) {

        userService.uploadProfilePic(file);
        return ResponseEntity.ok(new ApiResponse<>(AuthConstants.CODE_PROFILE_PIC_UPLOADED, AuthConstants.MSG_PROFILE_PIC_UPLOADED, null));
    }

    // ================= GET PROFILE PIC =================
    @GetMapping("/profile-pic")
    public ResponseEntity<Resource> getProfilePic() throws IOException {

        UserProfileResponse profile = userService.getProfile();

        if (profile.getProfilePic() == null) {
            return ResponseEntity.notFound().build();
        }

        Path path = Paths.get(uploadDir).resolve(profile.getProfilePic());
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

}
