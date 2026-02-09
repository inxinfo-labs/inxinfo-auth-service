package com.satishlabs.pandit.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pandits")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pandit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Link to auth User when created via "approve as pandit". Null if pandit was created manually. */
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String mobileNumber;

    private String address;
    private String city;
    private String state;
    private String pincode;

    @Column(length = 1000)
    private String bio;

    @Column(nullable = false)
    private Integer experienceYears;

    @Column(nullable = false)
    private BigDecimal hourlyRate;

    private String profileImageUrl;

    @ElementCollection
    @CollectionTable(name = "pandit_specializations", joinColumns = @JoinColumn(name = "pandit_id"))
    @Column(name = "specialization")
    private java.util.List<String> specializations;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PanditStatus status = PanditStatus.AVAILABLE;

    @Builder.Default
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

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
