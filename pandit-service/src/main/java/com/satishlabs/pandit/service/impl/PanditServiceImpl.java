package com.satishlabs.pandit.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.satishlabs.auth.exception.ResourceNotFoundException;
import com.satishlabs.pandit.client.AuthClient;
import com.satishlabs.pandit.dto.request.PanditBookingRequest;
import com.satishlabs.pandit.dto.request.PanditFromUserRequest;
import com.satishlabs.pandit.dto.request.PanditRequest;
import com.satishlabs.pandit.dto.request.ReleaseRequest;
import com.satishlabs.pandit.dto.request.ReserveRequest;
import com.satishlabs.pandit.dto.response.AvailabilityResponse;
import com.satishlabs.pandit.dto.response.PanditBookingResponse;
import com.satishlabs.pandit.dto.response.PanditResponse;
import com.satishlabs.pandit.dto.response.TimeSlot;
import com.satishlabs.pandit.entity.BookingStatus;
import com.satishlabs.pandit.entity.Pandit;
import com.satishlabs.pandit.entity.PanditBooking;
import com.satishlabs.pandit.entity.PanditReservation;
import com.satishlabs.pandit.entity.PanditStatus;
import com.satishlabs.pandit.repository.PanditBookingRepository;
import com.satishlabs.pandit.repository.PanditRepository;
import com.satishlabs.pandit.repository.PanditReservationRepository;
import com.satishlabs.pandit.service.PanditService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;

@Service
@RequiredArgsConstructor
@Transactional
public class PanditServiceImpl implements PanditService {

    private final PanditRepository panditRepository;
    private final PanditBookingRepository panditBookingRepository;
    private final PanditReservationRepository panditReservationRepository;
    @Qualifier("panditAuthClient") private final AuthClient authClient;

    @Override
    @Transactional(readOnly = true)
    public List<PanditResponse> getAllPandits() {
        return panditRepository.findByActiveTrue().stream()
                .map(this::mapToPanditResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PanditResponse> getAllPanditsForAdmin() {
        return panditRepository.findAll().stream()
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
    public PanditResponse createPandit(PanditRequest request) {
        if (panditRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Pandit with email already exists: " + request.getEmail());
        }
        Pandit pandit = Pandit.builder()
                .name(request.getName())
                .email(request.getEmail())
                .mobileNumber(request.getMobileNumber())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .bio(request.getBio())
                .experienceYears(request.getExperienceYears())
                .hourlyRate(request.getHourlyRate())
                .profileImageUrl(request.getProfileImageUrl())
                .specializations(request.getSpecializations() != null ? request.getSpecializations() : new ArrayList<>())
                .status(request.getStatus() != null ? request.getStatus() : PanditStatus.AVAILABLE)
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
        return mapToPanditResponse(panditRepository.save(pandit));
    }

    @Override
    public PanditResponse createPanditFromUser(PanditFromUserRequest request, String authorizationHeader) {
        if (panditRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new IllegalArgumentException("This user is already approved as a Pandit");
        }
        var userProfile = authClient.getUserById(request.getUserId(), authorizationHeader)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));
        if (panditRepository.findByEmail(userProfile.getEmail()).isPresent()) {
            throw new IllegalArgumentException("A Pandit with email " + userProfile.getEmail() + " already exists");
        }
        String name = userProfile.getName() != null && !userProfile.getName().isBlank()
                ? userProfile.getName()
                : userProfile.getEmail();
        Pandit pandit = Pandit.builder()
                .userId(request.getUserId())
                .name(name)
                .email(userProfile.getEmail())
                .mobileNumber(userProfile.getMobileNumber() != null ? userProfile.getMobileNumber() : "")
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .bio(request.getBio())
                .experienceYears(request.getExperienceYears())
                .hourlyRate(request.getHourlyRate())
                .specializations(request.getSpecializations() != null ? request.getSpecializations() : new ArrayList<>())
                .status(PanditStatus.AVAILABLE)
                .active(true)
                .build();
        return mapToPanditResponse(panditRepository.save(pandit));
    }

    @Override
    public PanditResponse updatePandit(Long id, PanditRequest request) {
        Pandit pandit = panditRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pandit not found with id: " + id));
        pandit.setName(request.getName());
        pandit.setEmail(request.getEmail());
        pandit.setMobileNumber(request.getMobileNumber());
        pandit.setAddress(request.getAddress());
        pandit.setCity(request.getCity());
        pandit.setState(request.getState());
        pandit.setPincode(request.getPincode());
        pandit.setBio(request.getBio());
        pandit.setExperienceYears(request.getExperienceYears());
        pandit.setHourlyRate(request.getHourlyRate());
        if (request.getProfileImageUrl() != null) pandit.setProfileImageUrl(request.getProfileImageUrl());
        if (request.getSpecializations() != null) pandit.setSpecializations(request.getSpecializations());
        if (request.getStatus() != null) pandit.setStatus(request.getStatus());
        if (request.getActive() != null) pandit.setActive(request.getActive());
        return mapToPanditResponse(panditRepository.save(pandit));
    }

    @Override
    public void deletePandit(Long id) {
        if (!panditRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pandit not found with id: " + id);
        }
        panditRepository.deleteById(id);
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
    public PanditBookingResponse bookPandit(PanditBookingRequest request, Long userId, String authorizationHeader) {
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
                .userId(userId)
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
        return mapToPanditBookingResponse(savedBooking, authorizationHeader);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PanditBookingResponse> getUserBookings(Long userId, String authorizationHeader) {
        return panditBookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(b -> mapToPanditBookingResponse(b, authorizationHeader))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PanditBookingResponse getBookingById(Long bookingId, Long userId, String authorizationHeader) {
        PanditBooking booking = panditBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Booking not found");
        }

        return mapToPanditBookingResponse(booking, authorizationHeader);
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
        return mapToPanditBookingResponse(updatedBooking, null);
    }

    @Override
    public Long reserve(ReserveRequest request) {
        Pandit pandit = panditRepository.findById(request.getPanditId())
                .orElseThrow(() -> new ResourceNotFoundException("Pandit not found with id: " + request.getPanditId()));
        if (panditReservationRepository.findByOrderIdAndStatus(request.getOrderId(), "ACTIVE").isPresent()) {
            throw new IllegalStateException("Order already has an active pandit reservation");
        }
        PanditReservation reservation = PanditReservation.builder()
                .orderId(request.getOrderId())
                .pandit(pandit)
                .status("ACTIVE")
                .build();
        return panditReservationRepository.save(reservation).getId();
    }

    @Override
    public void release(ReleaseRequest request) {
        panditReservationRepository.findByOrderIdAndStatus(request.getOrderId(), "ACTIVE")
                .ifPresent(r -> {
                    r.setStatus("RELEASED");
                    panditReservationRepository.save(r);
                });
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

    private PanditBookingResponse mapToPanditBookingResponse(PanditBooking booking, String authorizationHeader) {
        String userName = authClient.getUserById(booking.getUserId(), authorizationHeader)
                .map(u -> u.getName() != null ? u.getName() : u.getEmail())
                .orElse("");
        return PanditBookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .userName(userName)
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
