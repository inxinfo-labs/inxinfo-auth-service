package com.satishlabs.auth.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.satishlabs.auth.constants.ErrorCodes;
import com.satishlabs.auth.dto.response.ErrorResponse;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Single global exception handler. Maps exceptions to error codes and returns
 * consistent ErrorResponse. Never exposes stack traces to clients.
 * traceId is taken from MDC (set by TraceIdFilter).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String TRACE_ID_MDC = "traceId";

    private String getTraceId() {
        String traceId = MDC.get(TRACE_ID_MDC);
        return traceId != null ? traceId : "";
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {
        ErrorCodes code = ex.getErrorCode();
        log.warn("Business exception: errorCode={} path={} traceId={} message={}",
                code.getCode(), request.getRequestURI(), getTraceId(), ex.getMessage());
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ErrorResponse.of(code.getCode(), ex.getMessageForResponse(), getTraceId()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: path={} traceId={} message={}", request.getRequestURI(), getTraceId(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(ErrorCodes.USER_404_001.getCode(), ex.getMessage(), getTraceId()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String error = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getDefaultMessage())
                .orElse("Validation failed");
        log.warn("Validation failed: path={} traceId={} message={}", request.getRequestURI(), getTraceId(), error);
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ErrorCodes.AUTH_400_001.getCode(), error, getTraceId()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex, HttpServletRequest request) {
        ErrorCodes code = ex.getMessage() != null && ex.getMessage().toLowerCase().contains("mobile")
                ? ErrorCodes.AUTH_409_002
                : ErrorCodes.AUTH_409_001;
        log.warn("Duplicate resource: path={} traceId={} message={}", request.getRequestURI(), getTraceId(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(code.getCode(), ex.getMessage(), getTraceId()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        log.warn("Unauthorized: path={} traceId={} message={}", request.getRequestURI(), getTraceId(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(ErrorCodes.AUTH_401_001.getCode(), ex.getMessage(), getTraceId()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Invalid argument: path={} traceId={} message={}", request.getRequestURI(), getTraceId(), ex.getMessage());
        ErrorCodes code = mapIllegalArgToErrorCode(ex.getMessage());
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ErrorResponse.of(code.getCode(), ex.getMessage(), getTraceId()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied: path={} traceId={}", request.getRequestURI(), getTraceId());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(ErrorCodes.AUTH_403_001.getCode(), "Access denied", getTraceId()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        log.warn("Authentication failed: path={} traceId={} message={}", request.getRequestURI(), getTraceId(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(ErrorCodes.AUTH_401_001.getCode(), "Authentication failed", getTraceId()));
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ErrorResponse> handleRateLimit(RequestNotPermitted ex, HttpServletRequest request) {
        log.warn("Rate limit exceeded: path={} traceId={}", request.getRequestURI(), getTraceId());
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ErrorResponse.of(ErrorCodes.AUTH_429_001.getCode(), ErrorCodes.AUTH_429_001.getDefaultMessage(), getTraceId()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: path={} traceId={} errorCode={} message={}",
                request.getRequestURI(), getTraceId(), ErrorCodes.SYS_500_001.getCode(), ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(
                        ErrorCodes.SYS_500_001.getCode(),
                        "An unexpected error occurred. Please try again or contact support.",
                        getTraceId()));
    }

    private static ErrorCodes mapIllegalArgToErrorCode(String message) {
        if (message == null) return ErrorCodes.AUTH_400_001;
        String m = message.toLowerCase();
        if (m.contains("gmail") && m.contains("registration")) return ErrorCodes.AUTH_400_002;
        if (m.contains("gmail") && m.contains("reset")) return ErrorCodes.AUTH_400_003;
        if (m.contains("email or phone")) return ErrorCodes.AUTH_400_004;
        if (m.contains("otp") && m.contains("required")) return ErrorCodes.AUTH_400_005;
        if (m.contains("otp not found") || m.contains("expired")) return ErrorCodes.AUTH_400_006;
        if (m.contains("invalid otp")) return ErrorCodes.AUTH_400_008;
        if (m.contains("invalid token") || m.contains("password")) return ErrorCodes.AUTH_400_009;
        if (m.contains("reset link")) return ErrorCodes.AUTH_400_010;
        if (m.contains("user not found")) return ErrorCodes.USER_404_001;
        return ErrorCodes.AUTH_400_001;
    }
}
