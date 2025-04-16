package com.hoaithidev.vidsonet_backend.exception;


public class UnauthorizedException extends VidsonetException {
    public UnauthorizedException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }
}