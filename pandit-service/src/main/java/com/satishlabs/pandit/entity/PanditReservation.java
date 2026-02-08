package com.satishlabs.pandit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pandit_reservations", uniqueConstraints = @UniqueConstraint(columnNames = "orderId"))
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PanditReservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long orderId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pandit_id", nullable = false)
	private Pandit pandit;

	@Column(nullable = false)
	@Builder.Default
	private String status = "ACTIVE"; // ACTIVE, RELEASED

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
}
