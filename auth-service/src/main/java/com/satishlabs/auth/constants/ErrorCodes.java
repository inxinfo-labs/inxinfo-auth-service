package com.satishlabs.auth.constants;

import org.springframework.http.HttpStatus;

/**
 * Centralized error code strategy: &lt;DOMAIN&gt;_&lt;HTTP_CODE&gt;_&lt;SEQUENCE&gt;
 * Used for consistent API error responses and frontend mapping.
 */
public enum ErrorCodes {

    // ----- AUTH (4xx) -----
    AUTH_400_001("Validation failed"),
    AUTH_400_002("Only Gmail addresses are allowed for registration"),
    AUTH_400_003("Only Gmail addresses are allowed for password reset"),
    AUTH_400_004("Email or phone is required"),
    AUTH_400_005("Email/phone and OTP are required"),
    AUTH_400_006("OTP not found or expired. Please request a new one."),
    AUTH_400_007("OTP expired. Please request a new one."),
    AUTH_400_008("Invalid OTP."),
    AUTH_400_009("Invalid token or password (min 6 characters)."),
    AUTH_400_010("Invalid or expired reset link. Please request a new one."),

    AUTH_401_001("Invalid email/phone or password"),
    AUTH_401_002("Account is disabled"),
    AUTH_401_003("No account found with this email or phone number"),
    AUTH_401_004("User not found"),

    AUTH_403_001("Access denied"),

    AUTH_429_001("Too many requests. Please try again later."),

    AUTH_409_001("Email already registered"),
    AUTH_409_002("Mobile number already registered"),

    // ----- USER (4xx) -----
    USER_400_001("Validation failed"),
    USER_400_002("Old password incorrect"),
    USER_400_003("File is empty"),
    USER_400_004("Only JPG/PNG allowed"),

    USER_404_001("User not found"),
    USER_404_002("User not found with id: %s"),

    USER_500_001("Upload failed"),

    // ----- CONTACT -----
    CONTACT_503_001("Failed to send message. Please try again later."),

    // ----- SYSTEM -----
    SYS_400_001("Bad request"),
    SYS_404_001("Resource not found"),
    SYS_500_001("Unexpected system error"),
    SYS_500_002("Failed to send email"),
    ;

    private final String defaultMessage;

    ErrorCodes(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return name();
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public HttpStatus getHttpStatus() {
        String name = name();
        if (name.startsWith("AUTH_401") || name.startsWith("USER_401")) return HttpStatus.UNAUTHORIZED;
        if (name.startsWith("AUTH_403") || name.startsWith("USER_403")) return HttpStatus.FORBIDDEN;
        if (name.startsWith("AUTH_404") || name.startsWith("USER_404") || name.startsWith("SYS_404")) return HttpStatus.NOT_FOUND;
        if (name.startsWith("AUTH_429")) return HttpStatus.TOO_MANY_REQUESTS;
        if (name.startsWith("AUTH_409") || name.startsWith("USER_409")) return HttpStatus.CONFLICT;
        if (name.startsWith("CONTACT_503")) return HttpStatus.SERVICE_UNAVAILABLE;
        if (name.contains("_400_") || name.startsWith("SYS_400")) return HttpStatus.BAD_REQUEST;
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
