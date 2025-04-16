package com.hoaithidev.vidsonet_backend.exception;

import lombok.Getter;

import java.io.IOException;

@Getter
public class VidsonetException extends RuntimeException {

    private final ErrorCode errorCode;

    public VidsonetException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public VidsonetException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public VidsonetException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }


}