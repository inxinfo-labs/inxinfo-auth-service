package com.satishlabs.pandit.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.satishlabs.auth.entity.User;
import com.satishlabs.pandit.entity.BookingStatus;
import com.satishlabs.pandit.entity.Pandit;
import com.satishlabs.pandit.entity.PanditBooking;

@Repository
public interface PanditBookingRepository extends JpaRepository<PanditBooking, Long> {
    List<PanditBooking> findByUser(User user);
    List<PanditBooking> findByUserOrderByCreatedAtDesc(User user);
    List<PanditBooking> findByPandit(Pandit pandit);
    List<PanditBooking> findByPanditAndBookingDate(Pandit pandit, LocalDate date);
    List<PanditBooking> findByPanditAndBookingDateAndStatus(Pandit pandit, LocalDate date, BookingStatus status);
    List<PanditBooking> findByPanditAndBookingDateAndStartTimeLessThanAndEndTimeGreaterThan(
            Pandit pandit, LocalDate date, LocalTime time1, LocalTime time2);
    List<PanditBooking> findByStatus(BookingStatus status);
}
