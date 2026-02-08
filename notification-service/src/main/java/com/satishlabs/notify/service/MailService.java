package com.satishlabs.notify.service;

public interface MailService {
	void send(String to, String subject, String body);
}
