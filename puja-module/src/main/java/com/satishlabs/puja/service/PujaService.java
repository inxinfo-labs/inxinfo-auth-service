package com.satishlabs.puja.service;

import java.util.List;

import com.satishlabs.puja.dto.request.PujaBookingRequest;
import com.satishlabs.puja.dto.response.PujaBookingResponse;
import com.satishlabs.puja.dto.response.PujaTypeResponse;
import com.satishlabs.puja.entity.PujaCategory;

public interface PujaService {
    List<PujaTypeResponse> getAllPujaTypes();
    List<PujaTypeResponse> getPujaTypesByCategory(PujaCategory category);
    PujaTypeResponse getPujaTypeById(Long id);
    PujaBookingResponse bookPuja(PujaBookingRequest request, Long userId);
    List<PujaBookingResponse> getUserBookings(Long userId);
    PujaBookingResponse getBookingById(Long bookingId, Long userId);
    PujaBookingResponse updateBookingStatus(Long bookingId, String status);
}
