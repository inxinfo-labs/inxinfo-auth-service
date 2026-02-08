package com.satishlabs.auth.service;

public interface EmailService {
    void sendWelcomeEmail(String to, String name);
    void sendPasswordResetEmail(String to, String resetToken);
    void sendRegistrationConfirmation(String to, String name);
    void sendOrderConfirmation(String to, String orderNumber, String orderDetails);
    void sendOtpEmail(String to, String otpCode);
}
