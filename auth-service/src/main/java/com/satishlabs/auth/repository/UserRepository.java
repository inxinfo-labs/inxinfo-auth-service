package com.satishlabs.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.satishlabs.auth.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	Optional<User> findByEmailIgnoreCase(String email);
	Optional<User> findByMobileNumber(String mobileNumber);

	@Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:input) OR u.mobileNumber = :input")
	Optional<User> findByEmailOrMobileNumber(@Param("input") String input);

	boolean existsByEmail(String email);
	boolean existsByMobileNumber(String mobileNumber);
}
