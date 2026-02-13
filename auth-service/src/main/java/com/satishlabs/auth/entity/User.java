package com.satishlabs.auth.entity;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.satishlabs.auth.util.AuthProvider;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String firstName;
    private String lastName;
    /** Display name (e.g. firstName + " " + lastName); kept for backward compatibility */
    private String name;
    private String profilePic;

    private String mobileNumber;

    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String country;
    private String location;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    // 🔹 AUDIT FIELDS
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    private boolean enabled;

    /** True when user requested to join as PanditJi; admin approves via Admin → Pandit → Approve from user. Nullable for existing rows. */
    @Column(nullable = true)
    private Boolean wantsPanditApproval;

    /** When true, login requires a second step (OTP sent to email). Nullable for existing rows. */
    @Column(nullable = true)
    private Boolean twoFactorEnabled;

    // 🔹 DERIVED FIELD (NOT STORED)
    @Transient
    public Integer getAge() {
        if (dob == null) return null;
        return Period.between(dob, LocalDate.now()).getYears();
    }
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}

