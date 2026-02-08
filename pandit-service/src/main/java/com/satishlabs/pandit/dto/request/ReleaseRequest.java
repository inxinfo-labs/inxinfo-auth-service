package com.satishlabs.pandit.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReleaseRequest {
	@NotNull
	private Long orderId;
}
