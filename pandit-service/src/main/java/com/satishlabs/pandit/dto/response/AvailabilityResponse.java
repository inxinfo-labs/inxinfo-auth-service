package com.satishlabs.pandit.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AvailabilityResponse {
    private Long panditId;
    private LocalDate date;
    private List<TimeSlot> availableSlots;
}

