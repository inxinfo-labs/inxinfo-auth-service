package com.satishlabs.pandit.service;

import java.time.LocalDate;
import java.util.List;

import com.satishlabs.pandit.dto.request.PanditBookingRequest;
import com.satishlabs.pandit.dto.request.PanditFromUserRequest;
import com.satishlabs.pandit.dto.request.PanditRequest;
import com.satishlabs.pandit.dto.request.ReleaseRequest;
import com.satishlabs.pandit.dto.request.ReserveRequest;
import com.satishlabs.pandit.dto.response.AvailabilityResponse;
import com.satishlabs.pandit.dto.response.PanditBookingResponse;
import com.satishlabs.pandit.dto.response.PanditResponse;
import com.satishlabs.pandit.entity.PanditStatus;

public interface PanditService {
    List<PanditResponse> getAllPandits();
    List<PanditResponse> getAllPanditsForAdmin();
    List<PanditResponse> getAvailablePandits();
    List<PanditResponse> getPanditsByCity(String city);
    PanditResponse getPanditById(Long id);
    PanditResponse createPandit(PanditRequest request);
    /** Admin: approve an existing user (userId) as Pandit. Fetches name, email, mobile from auth. */
    PanditResponse createPanditFromUser(PanditFromUserRequest request, String authorizationHeader);
    PanditResponse updatePandit(Long id, PanditRequest request);
    void deletePandit(Long id);
    AvailabilityResponse checkAvailability(Long panditId, LocalDate date);
    PanditBookingResponse bookPandit(PanditBookingRequest request, Long userId, String authorizationHeader);
    List<PanditBookingResponse> getUserBookings(Long userId, String authorizationHeader);
    PanditBookingResponse getBookingById(Long bookingId, Long userId, String authorizationHeader);
    PanditBookingResponse updateBookingStatus(Long bookingId, String status);

    /** Mark booking as CONFIRMED after successful payment. */
    PanditBookingResponse confirmBookingPayment(Long bookingId, Long userId);

    /** Saga: reserve pandit for order (called by order-service) */
    Long reserve(ReserveRequest request);

    /** Saga: release reservation (compensation, called by order-service) */
    void release(ReleaseRequest request);
}
