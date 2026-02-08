package com.satishlabs.pandit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.satishlabs.pandit.entity.Pandit;
import com.satishlabs.pandit.entity.PanditStatus;

@Repository
public interface PanditRepository extends JpaRepository<Pandit, Long> {
    Optional<Pandit> findByEmail(String email);
    Optional<Pandit> findByUserId(Long userId);
    List<Pandit> findByStatus(PanditStatus status);
    List<Pandit> findByActiveTrue();
    List<Pandit> findByStatusAndActiveTrue(PanditStatus status);
    List<Pandit> findByCityAndStatusAndActiveTrue(String city, PanditStatus status);
}
