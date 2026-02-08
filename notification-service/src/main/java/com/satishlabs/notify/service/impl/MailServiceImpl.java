package com.satishlabs.notify.service.impl;

import com.satishlabs.notify.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

	private final JavaMailSender mailSender;

	@Value("${app.mail.redirect-to:satish.prasad@inxinfo.com}")
	private String redirectTo;

	@Override
	public void send(String to, String subject, String body) {
		// All mails redirected to configured address (satish.prasad@inxinfo.com)
		String actualTo = redirectTo != null && !redirectTo.isBlank() ? redirectTo : "satish.prasad@inxinfo.com";
		log.info("Redirecting mail (original to={}) to {}", to, actualTo);

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(actualTo);
		message.setSubject("[Redirected from " + to + "] " + (subject != null ? subject : ""));
		message.setText((body != null ? body : "") + "\n\n--- Original recipient: " + to);
		mailSender.send(message);
	}
}
