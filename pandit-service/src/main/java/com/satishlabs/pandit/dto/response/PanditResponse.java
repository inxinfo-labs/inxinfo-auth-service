package com.satishlabs.pandit.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.satishlabs.pandit.entity.PanditStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PanditResponse {
    private Long id;
    private String name;
    private String email;
    private String mobileNumber;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String bio;
    private Integer experienceYears;
    private BigDecimal hourlyRate;
    private String profileImageUrl;
    private List<String> specializations;
    private PanditStatus status;
    private boolean active;
}
