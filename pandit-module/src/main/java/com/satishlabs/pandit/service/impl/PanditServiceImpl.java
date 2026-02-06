package com.satishlabs.pandit.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.satishlabs.auth.entity.User;
import com.satishlabs.auth.exception.ResourceNotFoundException;
import com.satishlabs.auth.repository.UserRepository;
import com.satishlabs.pandit.dto.request.PanditBookingRequest;
import com.satishlabs.pandit.dto.response.AvailabilityResponse;
import com.satishlabs.pandit.dto.response.PanditBookingResponse;
import com.satishlabs.pandit.dto.response.PanditResponse;
import com.satishlabs.pandit.dto.response.TimeSlot;
import com.satishlabs.pandit.entity.BookingStatus;
import com.satishlabs.pandit.entity.Pandit;
import com.satishlabs.pandit.entity.PanditBooking;
import com.satishlabs.pandit.entity.PanditStatus;
import com.satishlabs.pandit.repository.PanditBookingRepository;
import com.satishlabs.pandit.repository.PanditRepository;
import com.satishlabs.pandit.service.PanditService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PanditServiceImpl implements PanditService {

    private final PanditRepository panditRepository;
    private final PanditBookingRepository panditBookingRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PanditResponse> getAllPandits() {
        return panditRepository.findByActiveTrue().stream()
                .map(this::mapToPanditResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PanditResponse> getAvailablePandits() {
        return panditRepository.findByStatusAndActiveTrue(PanditStatus.AVAILABLE).stream()
                .map(this::mapToPanditResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PanditResponse> getPanditsByCity(String city) {
        return panditRepository.findByCityAndStatusAndActiveTrue(city, PanditStatus.AVAILABLE).stream()
                .map(this::mapToPanditResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PanditResponse getPanditById(Long id) {
        Pandit pandit = panditRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pandit not found with id: " + id));
        return mapToPanditResponse(pandit);
    }

    @Override
    @Transactional(readOnly = true)
    public AvailabilityResponse checkAvailability(Long panditId, LocalDate date) {
        Pandit pandit = panditRepository.findById(panditId)
                .orElseThrow(() -> new ResourceNotFoundException("Pandit not found with id: " + panditId));

        List<PanditBooking> existingBookings = panditBookingRepository
                .findByPanditAndBookingDateAndStatus(pandit, date, BookingStatus.CONFIRMED);

        // Generate available time slots (9 AM to 8 PM, hourly slots)
        List<TimeSlot> availableSlots = new ArrayList<>();
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(20, 0);

        for (LocalTime slotStart = start; slotStart.isBefore(end); slotStart = slotStart.plusHours(1)) {
            LocalTime slotEnd = slotStart.plusHours(1);
            LocalTime finalSlotStart = slotStart;
            boolean isAvailable = existingBookings.stream().noneMatch(booking ->
                    (finalSlotStart.isBefore(booking.getEndTime()) && slotEnd.isAfter(booking.getStartTime()))
            );

            if (isAvailable) {
                availableSlots.add(TimeSlot.builder()
                        .startTime(slotStart)
                        .endTime(slotEnd)
                        .build());
            }
        }

        return AvailabilityResponse.builder()
                .panditId(panditId)
                .date(date)
                .availableSlots(availableSlots)
                .build();
    }

    @Override
    public PanditBookingResponse bookPandit(PanditBookingRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Pandit pandit = panditRepository.findById(request.getPanditId())
                .orElseThrow(() -> new ResourceNotFoundException("Pandit not found with id: " + request.getPanditId()));

        // Check if pandit is available
        if (pandit.getStatus() != PanditStatus.AVAILABLE) {
            throw new IllegalStateException("Pandit is not available for booking");
        }

        // Check if time slot is available
        List<PanditBooking> conflictingBookings = panditBookingRepository
                .findByPanditAndBookingDateAndStartTimeLessThanAndEndTimeGreaterThan(
                        pandit, request.getBookingDate(), request.getStartTime(),
                        request.getStartTime().plusHours(request.getDurationHours()));

        if (!conflictingBookings.isEmpty()) {
            throw new IllegalStateException("Time slot is not available");
        }

        LocalTime endTime = request.getStartTime().plusHours(request.getDurationHours());
        BigDecimal totalAmount = pandit.getHourlyRate()
                .multiply(BigDecimal.valueOf(request.getDurationHours()));

        PanditBooking booking = PanditBooking.builder()
                .user(user)
                .pandit(pandit)
                .bookingDate(request.getBookingDate())
                .startTime(request.getStartTime())
                .endTime(endTime)
                .durationHours(request.getDurationHours())
                .totalAmount(totalAmount)
                .status(BookingStatus.PENDING)
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .contactPhone(request.getContactPhone())
                .specialInstructions(request.getSpecialInstructions())
                .build();

        PanditBooking savedBooking = panditBookingRepository.save(booking);
        return mapToPanditBookingResponse(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PanditBookingResponse> getUserBookings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return panditBookingRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::mapToPanditBookingResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PanditBookingResponse getBookingById(Long bookingId, Long userId) {
        PanditBooking booking = panditBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Booking not found");
        }

        return mapToPanditBookingResponse(booking);
    }

    @Override
    public PanditBookingResponse updateBookingStatus(Long bookingId, String status) {
        PanditBooking booking = panditBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        try {
            booking.setStatus(BookingStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        PanditBooking updatedBooking = panditBookingRepository.save(booking);
        return mapToPanditBookingResponse(updatedBooking);
    }

    private PanditResponse mapToPanditResponse(Pandit pandit) {
        return PanditResponse.builder()
                .id(pandit.getId())
                .name(pandit.getName())
                .email(pandit.getEmail())
                .mobileNumber(pandit.getMobileNumber())
                .address(pandit.getAddress())
                .city(pandit.getCity())
                .state(pandit.getState())
                .pincode(pandit.getPincode())
                .bio(pandit.getBio())
                .experienceYears(pandit.getExperienceYears())
                .hourlyRate(pandit.getHourlyRate())
                .profileImageUrl(pandit.getProfileImageUrl())
                .specializations(pandit.getSpecializations())
                .status(pandit.getStatus())
                .active(pandit.isActive())
                .build();
    }

    private PanditBookingResponse mapToPanditBookingResponse(PanditBooking booking) {
        return PanditBookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getName())
                .pandit(mapToPanditResponse(booking.getPandit()))
                .bookingDate(booking.getBookingDate())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .durationHours(booking.getDurationHours())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .address(booking.getAddress())
                .city(booking.getCity())
                .state(booking.getState())
                .pincode(booking.getPincode())
                .contactPhone(booking.getContactPhone())
                .specialInstructions(booking.getSpecialInstructions())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
