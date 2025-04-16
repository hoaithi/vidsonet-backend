package com.hoaithidev.vidsonet_backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    // Common Errors 1000-1999
    SUCCESS(1000, "Success", HttpStatus.OK),
    INTERNAL_SERVER_ERROR(1001, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST(1002, "Invalid request", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1003, "Unauthorized", HttpStatus.UNAUTHORIZED),
    UNAUTHENTICATED(1008, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(1004, "Forbidden", HttpStatus.FORBIDDEN),
    RESOURCE_NOT_FOUND(1005, "Resource not found", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED(1006, "Method not allowed", HttpStatus.METHOD_NOT_ALLOWED),
    DUPLICATE_RESOURCE(1007, "Resource already exists", HttpStatus.CONFLICT),

    // Authentication Errors 2000-2999
    INVALID_CREDENTIALS(2000, "Invalid username or password", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN(2001, "Token has expired", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(2002, "Invalid token", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND(2003, "User not found", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS(2004, "Email already exists", HttpStatus.CONFLICT),
    USERNAME_ALREADY_EXISTS(2005, "Username already exists", HttpStatus.CONFLICT),
    CHANNEL_NAME_ALREADY_EXISTS(2006, "Channel name already exists", HttpStatus.CONFLICT),

    // User Errors 3000-3999
    INVALID_USER_DATA(3000, "Invalid user data", HttpStatus.BAD_REQUEST),
    USER_ALREADY_EXISTS(3001, "User already exists", HttpStatus.CONFLICT),
    USER_NOT_ACTIVE(3002, "User is not active", HttpStatus.FORBIDDEN),

    // Video Errors 4000-4999
    INVALID_VIDEO_DATA(4000, "Invalid video data", HttpStatus.BAD_REQUEST),
    VIDEO_NOT_FOUND(4001, "Video not found", HttpStatus.NOT_FOUND),
    VIDEO_UPLOAD_FAILED(4002, "Video upload failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PREMIUM_CONTENT(4003, "This is premium content", HttpStatus.FORBIDDEN),

    // Comment Errors 5000-5999
    COMMENT_NOT_FOUND(5000, "Comment not found", HttpStatus.NOT_FOUND),
    INVALID_COMMENT_DATA(5001, "Invalid comment data", HttpStatus.BAD_REQUEST),
    ALREADY_HEARTED(5002, "Comment is already hearted", HttpStatus.CONFLICT),
    NOT_VIDEO_OWNER(5003, "Only video owner can heart comments", HttpStatus.FORBIDDEN),

    // Subscription Errors 6000-6999
    ALREADY_SUBSCRIBED(6000, "Already subscribed to this channel", HttpStatus.CONFLICT),
    SUBSCRIPTION_NOT_FOUND(6001, "Subscription not found", HttpStatus.NOT_FOUND),
    CANNOT_SUBSCRIBE_SELF(6002, "Cannot subscribe to your own channel", HttpStatus.BAD_REQUEST),

    // Playlist Errors 7000-7999
    PLAYLIST_NOT_FOUND(7000, "Playlist not found", HttpStatus.NOT_FOUND),
    INVALID_PLAYLIST_DATA(7001, "Invalid playlist data", HttpStatus.BAD_REQUEST),
    VIDEO_ALREADY_IN_PLAYLIST(7002, "Video already in playlist", HttpStatus.CONFLICT),
    VIDEO_NOT_IN_PLAYLIST(7003, "Video not in playlist", HttpStatus.NOT_FOUND),

    // Payment & Membership Errors 8000-8999
    PAYMENT_FAILED(8000, "Payment failed", HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_DATA(8001, "Invalid payment data", HttpStatus.BAD_REQUEST),
    MEMBERSHIP_NOT_FOUND(8002, "Membership not found", HttpStatus.NOT_FOUND),
    ALREADY_MEMBER(8003, "User is already a member", HttpStatus.CONFLICT);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}