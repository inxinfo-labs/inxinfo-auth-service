package com.satishlabs.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard success response: success=true, data (optional).
 * traceId is returned in response header X-Trace-Id for all responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessResponse<T> {

    private boolean success;
    private T data;

    public static <T> SuccessResponse<T> of(T data) {
        return SuccessResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }
}
