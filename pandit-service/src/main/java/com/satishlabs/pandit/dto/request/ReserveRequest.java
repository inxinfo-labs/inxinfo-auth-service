package com.satishlabs.pandit.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReserveRequest {
	@NotNull
	private Long orderId;
	@NotNull
	private Long panditId;
}
