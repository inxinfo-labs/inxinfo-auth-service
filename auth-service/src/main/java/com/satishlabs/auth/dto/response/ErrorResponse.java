package com.satishlabs.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard error response for all API failures.
 * Contract: success=false, errorCode, message, traceId (for debugging).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private boolean success;
    private String errorCode;
    private String message;
    private String traceId;

    public static ErrorResponse of(String errorCode, String message, String traceId) {
        return ErrorResponse.builder()
                .success(false)
                .errorCode(errorCode)
                .message(message != null ? message : "An error occurred")
                .traceId(traceId)
                .build();
    }
}
