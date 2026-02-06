package com.satishlabs.puja.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.satishlabs.puja.entity.PujaCategory;
import com.satishlabs.puja.entity.PujaType;

@Repository
public interface PujaTypeRepository extends JpaRepository<PujaType, Long> {
    Optional<PujaType> findByName(String name);
    List<PujaType> findByCategory(PujaCategory category);
    List<PujaType> findByActiveTrue();
    List<PujaType> findByCategoryAndActiveTrue(PujaCategory category);
}
