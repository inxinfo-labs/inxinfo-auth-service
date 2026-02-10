package com.satishlabs.auth.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.satishlabs.auth.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@inxinfo.com}")
    private String fromEmail;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    @Value("${app.contact.email:satish.prasad@inxinfo.com}")
    private String adminContactEmail;

    // Check if mail sender is properly configured (from address must be set and valid for sending)
    private boolean isMailConfigured() {
        if (!mailEnabled) {
            return false;
        }
        return fromEmail != null && !fromEmail.isBlank();
    }

    @Override
    public void sendWelcomeEmail(String to, String name) {
        if (!isMailConfigured()) {
            log.warn("Email service not configured. Set spring.mail.username and spring.mail.password in application.yml");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Welcome to INXINFO Labs!");
            message.setText(String.format(
                "Dear %s,\n\n" +
                "Welcome to INXINFO Labs!\n\n" +
                "We're excited to have you on board. Your account has been successfully created.\n\n" +
                "You can now access all our services including:\n" +
                "- Puja Booking Services\n" +
                "- Pandit Booking\n" +
                "- Order Management\n\n" +
                "If you have any questions, feel free to contact our support team.\n\n" +
                "Best regards,\n" +
                "INXINFO Labs Team",
                name
            ));
            mailSender.send(message);
            log.info("Welcome email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}. Error: {}", to, e.getMessage());
            // Don't throw exception - email failure shouldn't break registration
        }
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        if (!isMailConfigured()) {
            log.warn("Email service not configured. Cannot send password reset email.");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Password Reset Request - INXINFO Labs");
            String link = (resetToken != null && resetToken.startsWith("http")) ? resetToken : null;
            String body = "Dear User,\n\nYou have requested to reset your password.\n\n"
                + (link != null
                    ? "Click the link below to reset your password (valid for 1 hour):\n" + link + "\n\n"
                    : "Reset Token: " + resetToken + "\n\nPlease use this token on the reset password page. This token will expire in 1 hour.\n\n")
                + "If you did not request this, please ignore this email.\n\nBest regards,\nINXINFO Labs Team";
            message.setText(body);
            mailSender.send(message);
            log.info("Password reset email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}. Error: {}", to, e.getMessage());
        }
    }

    @Override
    public void sendRegistrationConfirmation(String to, String name) {
        if (!isMailConfigured()) {
            log.warn("Email service not configured. Skipping registration confirmation email.");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Registration Confirmed - INXINFO Labs");
            message.setText(String.format(
                "Dear %s,\n\n" +
                "Thank you for registering with INXINFO Labs!\n\n" +
                "Your registration has been confirmed. You can now log in and start using our services.\n\n" +
                "Best regards,\n" +
                "INXINFO Labs Team",
                name
            ));
            mailSender.send(message);
            log.info("Registration confirmation email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send registration confirmation email to: {}. Error: {}", to, e.getMessage());
        }
    }

    @Override
    public void sendOrderConfirmation(String to, String orderNumber, String orderDetails) {
        if (!isMailConfigured()) {
            log.warn("Email service not configured. Skipping order confirmation email.");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Order Confirmation - INXINFO Labs");
            message.setText(String.format(
                "Dear Customer,\n\n" +
                "Your order has been confirmed!\n\n" +
                "Order Number: %s\n\n" +
                "Order Details:\n%s\n\n" +
                "Thank you for your business!\n\n" +
                "Best regards,\n" +
                "INXINFO Labs Team",
                orderNumber, orderDetails
            ));
            mailSender.send(message);
            log.info("Order confirmation email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send order confirmation email to: {}. Error: {}", to, e.getMessage());
        }
    }

    @Override
    public void sendOtpEmail(String to, String otpCode) {
        if (!isMailConfigured()) {
            log.warn("Email not configured. OTP for {} would be: {}", to, otpCode);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Your login OTP - INXINFO Labs");
            message.setText(String.format(
                "Your one-time password for sign in is: %s\n\n" +
                "This code expires in 10 minutes. Do not share it with anyone.\n\n" +
                "Best regards,\nINXINFO Labs Team",
                otpCode
            ));
            mailSender.send(message);
            log.info("OTP email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}. Error: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    @Override
    public void sendContactToAdmin(String fromName, String fromEmail, String subject, String messageBody) {
        if (!isMailConfigured()) {
            log.warn("Email not configured. Contact form message from {} would be sent to admin.", fromEmail);
            return;
        }
        String to = adminContactEmail != null && !adminContactEmail.isBlank() ? adminContactEmail : "satish.prasad@inxinfo.com";
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(this.fromEmail);
            message.setTo(to);
            if (fromEmail != null && !fromEmail.isBlank()) {
                message.setReplyTo(fromEmail);
            }
            message.setSubject(subject != null && !subject.isBlank() ? subject : "Contact from INXINFO Labs");
            message.setText(
                "Message from website contact form:\n\n"
                + "From: " + (fromName != null ? fromName : "") + " <" + (fromEmail != null ? fromEmail : "") + ">\n\n"
                + (messageBody != null ? messageBody : "")
            );
            mailSender.send(message);
            log.info("Contact form email sent to admin: {}", to);
        } catch (Exception e) {
            log.error("Failed to send contact email to admin: {}", e.getMessage());
            throw new RuntimeException("Failed to send message. Please try again or email us directly.", e);
        }
    }

    @Override
    public void sendPanditApplicationNotify(String applicantName, String applicantEmail, Long userId) {
        if (!isMailConfigured()) {
            log.warn("Email not configured. Pandit application from {} would be notified to admin.", applicantEmail);
            return;
        }
        String to = adminContactEmail != null && !adminContactEmail.isBlank() ? adminContactEmail : "satish.prasad@inxinfo.com";
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("INXINFO Labs: New PanditJi application");
            message.setText(String.format(
                "A user has requested to join as PanditJi.\n\n"
                + "Name: %s\n"
                + "Email: %s\n"
                + "User ID: %s\n\n"
                + "Please log in to Admin → Pandit → Approve from user to approve this applicant.",
                applicantName != null ? applicantName : "",
                applicantEmail != null ? applicantEmail : "",
                userId != null ? userId : ""
            ));
            mailSender.send(message);
            log.info("Pandit application notification sent to admin: {}", to);
        } catch (Exception e) {
            log.error("Failed to send Pandit application notify: {}", e.getMessage());
        }
    }
}
