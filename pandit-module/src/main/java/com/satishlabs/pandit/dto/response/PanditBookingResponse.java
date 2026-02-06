package com.satishlabs.pandit.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.satishlabs.pandit.entity.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PanditBookingResponse {
    private Long id;
    private Long userId;
    private String userName;
    private PanditResponse pandit;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationHours;
    private BigDecimal totalAmount;
    private BookingStatus status;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String contactPhone;
    private String specialInstructions;
    private LocalDateTime createdAt;
}
