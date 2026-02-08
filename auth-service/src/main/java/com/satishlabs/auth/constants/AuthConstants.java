package com.satishlabs.auth.constants;

/**
 * Central constants for Auth module: response codes and messages.
 * Use these in controllers and exception handlers for consistent API responses.
 */
public final class AuthConstants {

    private AuthConstants() {}

    // Success codes (1xxx)
    public static final int CODE_SUCCESS = 1000;
    public static final int CODE_OTP_SENT = 1001;
    public static final int CODE_PROFILE_FETCHED = 1002;
    public static final int CODE_PROFILE_UPDATED = 1003;
    public static final int CODE_PASSWORD_UPDATED = 1004;
    public static final int CODE_PROFILE_PIC_UPLOADED = 1005;

    // Client/validation error codes (4xxx)
    public static final int CODE_VALIDATION_ERROR = 4001;
    public static final int CODE_NOT_FOUND = 4004;
    public static final int CODE_DUPLICATE = 4009;
    public static final int CODE_UNAUTHORIZED = 4010;

    // Server error (5xxx)
    public static final int CODE_SERVER_ERROR = 5000;

    // Messages
    public static final String MSG_OTP_SENT = "OTP sent successfully";
    public static final String MSG_PROFILE_FETCHED = "Profile fetched successfully";
    public static final String MSG_PROFILE_UPDATED = "Profile updated successfully";
    public static final String MSG_PASSWORD_UPDATED = "Password updated successfully";
    public static final String MSG_PROFILE_PIC_UPLOADED = "Profile picture uploaded successfully";
}
