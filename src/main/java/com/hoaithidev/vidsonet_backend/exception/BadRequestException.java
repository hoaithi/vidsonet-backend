package com.hoaithidev.vidsonet_backend.exception;

public class BadRequestException extends VidsonetException {
    public BadRequestException(String message) {
        super(ErrorCode.INVALID_REQUEST, message);
    }
}