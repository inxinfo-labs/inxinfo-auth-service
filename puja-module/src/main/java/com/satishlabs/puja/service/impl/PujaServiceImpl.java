package com.satishlabs.puja.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.satishlabs.auth.entity.User;
import com.satishlabs.auth.exception.ResourceNotFoundException;
import com.satishlabs.auth.repository.UserRepository;
import com.satishlabs.puja.dto.request.PujaBookingRequest;
import com.satishlabs.puja.dto.response.PujaBookingResponse;
import com.satishlabs.puja.dto.response.PujaTypeResponse;
import com.satishlabs.puja.entity.BookingStatus;
import com.satishlabs.puja.entity.PujaBooking;
import com.satishlabs.puja.entity.PujaCategory;
import com.satishlabs.puja.entity.PujaType;
import com.satishlabs.puja.repository.PujaBookingRepository;
import com.satishlabs.puja.repository.PujaTypeRepository;
import com.satishlabs.puja.service.PujaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PujaServiceImpl implements PujaService {

    private final PujaTypeRepository pujaTypeRepository;
    private final PujaBookingRepository pujaBookingRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PujaTypeResponse> getAllPujaTypes() {
        return pujaTypeRepository.findByActiveTrue().stream()
                .map(this::mapToPujaTypeResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PujaTypeResponse> getPujaTypesByCategory(PujaCategory category) {
        return pujaTypeRepository.findByCategoryAndActiveTrue(category).stream()
                .map(this::mapToPujaTypeResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PujaTypeResponse getPujaTypeById(Long id) {
        PujaType pujaType = pujaTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Puja type not found with id: " + id));
        return mapToPujaTypeResponse(pujaType);
    }

    @Override
    public PujaBookingResponse bookPuja(PujaBookingRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        PujaType pujaType = pujaTypeRepository.findById(request.getPujaTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Puja type not found with id: " + request.getPujaTypeId()));

        PujaBooking booking = PujaBooking.builder()
                .user(user)
                .pujaType(pujaType)
                .bookingDate(request.getBookingDate())
                .bookingTime(request.getBookingTime())
                .specialInstructions(request.getSpecialInstructions())
                .totalAmount(pujaType.getPrice())
                .status(BookingStatus.PENDING)
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .contactPhone(request.getContactPhone())
                .build();

        PujaBooking savedBooking = pujaBookingRepository.save(booking);
        return mapToPujaBookingResponse(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PujaBookingResponse> getUserBookings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return pujaBookingRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::mapToPujaBookingResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PujaBookingResponse getBookingById(Long bookingId, Long userId) {
        PujaBooking booking = pujaBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        
        if (!booking.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Booking not found");
        }
        
        return mapToPujaBookingResponse(booking);
    }

    @Override
    public PujaBookingResponse updateBookingStatus(Long bookingId, String status) {
        PujaBooking booking = pujaBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        
        try {
            booking.setStatus(BookingStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        
        PujaBooking updatedBooking = pujaBookingRepository.save(booking);
        return mapToPujaBookingResponse(updatedBooking);
    }

    private PujaTypeResponse mapToPujaTypeResponse(PujaType pujaType) {
        return PujaTypeResponse.builder()
                .id(pujaType.getId())
                .name(pujaType.getName())
                .description(pujaType.getDescription())
                .price(pujaType.getPrice())
                .imageUrl(pujaType.getImageUrl())
                .durationMinutes(pujaType.getDurationMinutes())
                .category(pujaType.getCategory())
                .active(pujaType.isActive())
                .build();
    }

    private PujaBookingResponse mapToPujaBookingResponse(PujaBooking booking) {
        return PujaBookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getName())
                .pujaType(mapToPujaTypeResponse(booking.getPujaType()))
                .bookingDate(booking.getBookingDate())
                .bookingTime(booking.getBookingTime())
                .specialInstructions(booking.getSpecialInstructions())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .address(booking.getAddress())
                .city(booking.getCity())
                .state(booking.getState())
                .pincode(booking.getPincode())
                .contactPhone(booking.getContactPhone())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
