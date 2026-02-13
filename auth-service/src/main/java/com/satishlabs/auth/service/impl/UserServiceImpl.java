package com.satishlabs.auth.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.satishlabs.auth.dto.mapper.UserMapper;
import com.satishlabs.auth.dto.request.UpdatePasswordRequest;
import com.satishlabs.auth.dto.request.UpdateProfileRequest;
import com.satishlabs.auth.dto.response.UserProfileResponse;
import com.satishlabs.auth.entity.Role;
import com.satishlabs.auth.entity.User;
import com.satishlabs.auth.exception.ResourceNotFoundException;
import com.satishlabs.auth.repository.UserRepository;
import com.satishlabs.auth.service.EmailService;
import com.satishlabs.auth.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.upload.profile-pic-path:uploads/profile-pics}")
    private String uploadDir;

    @Autowired
    UserMapper userMapper;

    private String currentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public UserProfileResponse getProfile() {
        User user = userRepository.findByEmail(currentUserEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toProfileResponse(user);
    }

    @Override
    public UserProfileResponse getProfileById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toProfileResponse(user);
    }

    @Override
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toProfileResponse)
                .toList();
    }

    @Override
    public void updateUserRole(Long id, Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setRole(role);
        if (role == Role.PANDIT) {
            user.setWantsPanditApproval(false);
        }
        userRepository.save(user);
    }

    @Override
    public void setUserEnabled(Long id, boolean enabled) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    @Override
    public void updateProfile(UpdateProfileRequest request) {
        User user = userRepository.findByEmail(currentUserEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setName((request.getFirstName() + " " + request.getLastName()).trim());
        user.setMobileNumber(request.getMobileNumber());
        user.setDob(request.getDob());
        user.setGender(request.getGender());
        user.setCountry(request.getCountry());
        user.setLocation(request.getLocation());

        userRepository.save(user);

        try {
            emailService.sendProfileUpdateNotify(
                user.getName(),
                user.getEmail(),
                user.getId(),
                user.getRole() != null ? user.getRole().name() : "USER"
            );
        } catch (Exception e) {
            log.warn("Failed to send admin profile-update notify: userId={} error={}", user.getId(), e.getMessage());
        }
    }

    @Override
    public void updatePassword(UpdatePasswordRequest request) {
        User user = userRepository.findByEmail(currentUserEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void uploadProfilePic(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (!List.of("image/jpeg", "image/png").contains(file.getContentType())) {
            throw new RuntimeException("Only JPG/PNG allowed");
        }

        User user = userRepository.findByEmail(currentUserEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            Files.createDirectories(Paths.get(uploadDir));

            String ext = file.getOriginalFilename()
                    .substring(file.getOriginalFilename().lastIndexOf("."));

            String fileName = "user-" + user.getId() + ext;

            Path path = Paths.get(uploadDir).resolve(fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            user.setProfilePic(fileName);
            userRepository.save(user);

            try {
                emailService.sendProfileUpdateNotify(
                    user.getName(),
                    user.getEmail(),
                    user.getId(),
                    user.getRole() != null ? user.getRole().name() : "USER"
                );
            } catch (Exception ex) {
                log.warn("Failed to send admin profile-pic-update notify: userId={} error={}", user.getId(), ex.getMessage());
            }

        } catch (IOException e) {
            throw new RuntimeException("Upload failed", e);
        }
    }

}
