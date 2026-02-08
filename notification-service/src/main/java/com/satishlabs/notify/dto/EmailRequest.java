package com.satishlabs.notify.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailRequest {

	@NotBlank(message = "To is required")
	private String to;

	private String subject = "";
	private String body = "";
}
