package com.satishlabs.auth.service;

public interface EmailService {
    void sendWelcomeEmail(String to, String name);
    void sendPasswordResetEmail(String to, String resetToken);
    void sendRegistrationConfirmation(String to, String name);
    void sendOrderConfirmation(String to, String orderNumber, String orderDetails);
    void sendOtpEmail(String to, String otpCode);
    /** Send contact form message to admin (e.g. satish.prasad@inxinfo.com). */
    void sendContactToAdmin(String fromName, String fromEmail, String subject, String messageBody);
    /** Notify admin that a user has requested to join as PanditJi. */
    void sendPanditApplicationNotify(String applicantName, String applicantEmail, Long userId);
    /** Notify admin when any new customer registers. */
    void sendNewCustomerNotify(String customerName, String customerEmail);
}
