package com.satishlabs.puja.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.satishlabs.auth.entity.User;
import com.satishlabs.puja.entity.BookingStatus;
import com.satishlabs.puja.entity.PujaBooking;

@Repository
public interface PujaBookingRepository extends JpaRepository<PujaBooking, Long> {
    List<PujaBooking> findByUser(User user);
    List<PujaBooking> findByUserOrderByCreatedAtDesc(User user);
    List<PujaBooking> findByStatus(BookingStatus status);
    List<PujaBooking> findByBookingDate(LocalDate date);
    List<PujaBooking> findByBookingDateAndStatus(LocalDate date, BookingStatus status);
}
