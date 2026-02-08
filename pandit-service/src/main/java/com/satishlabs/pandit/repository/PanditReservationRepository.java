package com.satishlabs.pandit.repository;

import com.satishlabs.pandit.entity.PanditReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PanditReservationRepository extends JpaRepository<PanditReservation, Long> {
	Optional<PanditReservation> findByOrderIdAndStatus(Long orderId, String status);
}
