package com.satishlabs.notify.controller;

import com.satishlabs.notify.dto.EmailRequest;
import com.satishlabs.notify.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class NotifyController {

	private final MailService mailService;

	@PostMapping("/email")
	public ResponseEntity<Map<String, Object>> sendEmail(@Valid @RequestBody EmailRequest request) {
		System.out.println("==================================: "+request.getBody());
		mailService.send(request.getTo(), request.getSubject(), request.getBody());
		return ResponseEntity.ok(Map.of("code", 5001, "message", "Email sent successfully", "data", null));
	}
}
