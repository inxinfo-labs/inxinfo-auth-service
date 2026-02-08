package com.satishlabs.auth.constants;

/**
 * Central API response/error codes used across the application.
 * Auth module uses 1xxx (success), 4xxx (client errors), 5xxx (server).
 */
public final class ApiResponseCodes {

    private ApiResponseCodes() {}

    public static final int SUCCESS = 1000;
    public static final int VALIDATION_ERROR = 4001;
    public static final int NOT_FOUND = 4004;
    public static final int UNAUTHORIZED = 4010;
    public static final int DUPLICATE = 4009;
    public static final int SERVER_ERROR = 5000;
}
