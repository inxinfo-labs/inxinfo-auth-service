package com.satishlabs.pandit.service;

import java.time.LocalDate;
import java.util.List;

import com.satishlabs.pandit.dto.request.PanditBookingRequest;
import com.satishlabs.pandit.dto.response.AvailabilityResponse;
import com.satishlabs.pandit.dto.response.PanditBookingResponse;
import com.satishlabs.pandit.dto.response.PanditResponse;
import com.satishlabs.pandit.entity.PanditStatus;

public interface PanditService {
    List<PanditResponse> getAllPandits();
    List<PanditResponse> getAvailablePandits();
    List<PanditResponse> getPanditsByCity(String city);
    PanditResponse getPanditById(Long id);
    AvailabilityResponse checkAvailability(Long panditId, LocalDate date);
    PanditBookingResponse bookPandit(PanditBookingRequest request, Long userId);
    List<PanditBookingResponse> getUserBookings(Long userId);
    PanditBookingResponse getBookingById(Long bookingId, Long userId);
    PanditBookingResponse updateBookingStatus(Long bookingId, String status);
}
