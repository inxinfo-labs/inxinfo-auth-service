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

import com.satishlabs.auth.dto.request.UpdatePasswordRequest;
import com.satishlabs.auth.dto.request.UpdateProfileRequest;
import com.satishlabs.auth.dto.response.ApiResponse;
import com.satishlabs.auth.dto.response.UserProfileResponse;
import com.satishlabs.auth.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Value("${app.upload.profile-pic-path}")
    private String uploadDir;

    // ================= GET PROFILE =================
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile() {
        return ResponseEntity.ok(
                new ApiResponse<>(1002, "Profile fetched successfully", userService.getProfile())
        );
    }

    // ================= UPDATE PROFILE =================
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {

        userService.updateProfile(request);
        return ResponseEntity.ok(new ApiResponse<>(1003, "Profile updated successfully", null));
    }

    // ================= UPDATE PASSWORD =================
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request) {

        userService.updatePassword(request);
        return ResponseEntity.ok(new ApiResponse<>(1004, "Password updated successfully", null));
    }

    // ================= UPLOAD PROFILE PIC =================
    @PostMapping("/profile-pic")
    public ResponseEntity<ApiResponse<Void>> uploadProfilePic(
            @RequestParam("file") MultipartFile file) {

        userService.uploadProfilePic(file);

        // ✅ dispatch event frontend can listen
        return ResponseEntity.ok(new ApiResponse<>(1005, "Profile picture uploaded successfully", null));
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
