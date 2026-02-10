package com.satishlabs.puja.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "puja_types")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PujaType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    private String imageUrl;

    @Column(nullable = false)
    private Integer durationMinutes; // Duration in minutes

    /** Ritual type (e.g. Griha Pravesh, Satyanarayan Puja). Used in bookings and Pandit specializations. */
    @Enumerated(EnumType.STRING)
    @Column(name = "ritual_type")
    private RitualType ritualType;

    /** Legacy grouping; optional when ritualType is set. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private PujaCategory category;

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
