package com.satishlabs.puja.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.satishlabs.auth.exception.ResourceNotFoundException;
import com.satishlabs.puja.client.AuthClient;
import com.satishlabs.puja.dto.request.PujaBookingRequest;
import com.satishlabs.puja.dto.request.PujaTypeRequest;
import com.satishlabs.puja.dto.response.PujaBookingResponse;
import com.satishlabs.puja.dto.response.PujaTypeResponse;
import com.satishlabs.puja.entity.BookingStatus;
import com.satishlabs.puja.entity.PujaBooking;
import com.satishlabs.puja.entity.PujaCategory;
import com.satishlabs.puja.entity.PujaType;
import com.satishlabs.puja.entity.RitualType;
import com.satishlabs.puja.repository.PujaBookingRepository;
import com.satishlabs.puja.repository.PujaTypeRepository;
import com.satishlabs.puja.service.PujaService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;

@Service
@RequiredArgsConstructor
@Transactional
public class PujaServiceImpl implements PujaService {

    private final PujaTypeRepository pujaTypeRepository;
    private final PujaBookingRepository pujaBookingRepository;
    @Qualifier("pujaAuthClient") private final AuthClient authClient;

    @Override
    @Transactional(readOnly = true)
    public List<PujaTypeResponse> getAllPujaTypes() {
        return pujaTypeRepository.findByActiveTrue().stream()
                .map(this::mapToPujaTypeResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PujaTypeResponse> getAllPujaTypesForAdmin() {
        return pujaTypeRepository.findAll().stream()
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
    public PujaTypeResponse createPujaType(PujaTypeRequest request) {
        PujaType pujaType = PujaType.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .durationMinutes(request.getDurationMinutes())
                .ritualType(request.getRitualType())
                .category(request.getCategory())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
        return mapToPujaTypeResponse(pujaTypeRepository.save(pujaType));
    }

    @Override
    public PujaTypeResponse updatePujaType(Long id, PujaTypeRequest request) {
        PujaType pujaType = pujaTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Puja type not found with id: " + id));
        pujaType.setName(request.getName());
        pujaType.setDescription(request.getDescription());
        pujaType.setPrice(request.getPrice());
        pujaType.setImageUrl(request.getImageUrl());
        pujaType.setDurationMinutes(request.getDurationMinutes());
        if (request.getRitualType() != null) pujaType.setRitualType(request.getRitualType());
        if (request.getCategory() != null) pujaType.setCategory(request.getCategory());
        if (request.getActive() != null) pujaType.setActive(request.getActive());
        return mapToPujaTypeResponse(pujaTypeRepository.save(pujaType));
    }

    @Override
    public void deletePujaType(Long id) {
        if (!pujaTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Puja type not found with id: " + id);
        }
        pujaTypeRepository.deleteById(id);
    }

    @Override
    public PujaBookingResponse bookPuja(PujaBookingRequest request, Long userId, String authorizationHeader) {
        PujaType pujaType = pujaTypeRepository.findById(request.getPujaTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Puja type not found with id: " + request.getPujaTypeId()));

        PujaBooking booking = PujaBooking.builder()
                .userId(userId)
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
        return mapToPujaBookingResponse(savedBooking, authorizationHeader);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PujaBookingResponse> getUserBookings(Long userId, String authorizationHeader) {
        return pujaBookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(b -> mapToPujaBookingResponse(b, authorizationHeader))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PujaBookingResponse getBookingById(Long bookingId, Long userId, String authorizationHeader) {
        PujaBooking booking = pujaBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Booking not found");
        }

        return mapToPujaBookingResponse(booking, authorizationHeader);
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
        return mapToPujaBookingResponse(updatedBooking, null);
    }

    private PujaTypeResponse mapToPujaTypeResponse(PujaType pujaType) {
        return PujaTypeResponse.builder()
                .id(pujaType.getId())
                .name(pujaType.getName())
                .description(pujaType.getDescription())
                .price(pujaType.getPrice())
                .imageUrl(pujaType.getImageUrl())
                .durationMinutes(pujaType.getDurationMinutes())
                .ritualType(pujaType.getRitualType())
                .category(pujaType.getCategory())
                .active(pujaType.isActive())
                .createdAt(pujaType.getCreatedAt())
                .updatedAt(pujaType.getUpdatedAt())
                .build();
    }

    private PujaBookingResponse mapToPujaBookingResponse(PujaBooking booking, String authorizationHeader) {
        String userName = authClient.getUserById(booking.getUserId(), authorizationHeader)
                .map(u -> u.getName() != null ? u.getName() : u.getEmail())
                .orElse("");
        return PujaBookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .userName(userName)
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
