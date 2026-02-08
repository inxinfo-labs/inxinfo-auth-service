package com.satishlabs.puja.service;

import java.util.List;

import com.satishlabs.puja.dto.request.PujaBookingRequest;
import com.satishlabs.puja.dto.request.PujaTypeRequest;
import com.satishlabs.puja.dto.response.PujaBookingResponse;
import com.satishlabs.puja.dto.response.PujaTypeResponse;
import com.satishlabs.puja.entity.PujaCategory;

public interface PujaService {
    List<PujaTypeResponse> getAllPujaTypes();
    List<PujaTypeResponse> getAllPujaTypesForAdmin();
    List<PujaTypeResponse> getPujaTypesByCategory(PujaCategory category);
    PujaTypeResponse getPujaTypeById(Long id);
    PujaTypeResponse createPujaType(PujaTypeRequest request);
    PujaTypeResponse updatePujaType(Long id, PujaTypeRequest request);
    void deletePujaType(Long id);
    PujaBookingResponse bookPuja(PujaBookingRequest request, Long userId, String authorizationHeader);
    List<PujaBookingResponse> getUserBookings(Long userId, String authorizationHeader);
    PujaBookingResponse getBookingById(Long bookingId, Long userId, String authorizationHeader);
    PujaBookingResponse updateBookingStatus(Long bookingId, String status);
}
