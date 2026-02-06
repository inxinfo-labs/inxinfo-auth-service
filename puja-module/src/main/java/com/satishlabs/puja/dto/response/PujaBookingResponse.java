package com.satishlabs.puja.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.satishlabs.puja.entity.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PujaBookingResponse {
    private Long id;
    private Long userId;
    private String userName;
    private PujaTypeResponse pujaType;
    private LocalDate bookingDate;
    private LocalTime bookingTime;
    private String specialInstructions;
    private BigDecimal totalAmount;
    private BookingStatus status;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String contactPhone;
    private LocalDateTime createdAt;
}
