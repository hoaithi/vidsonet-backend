package com.hoaithidev.vidsonet_backend.exception;

import lombok.Getter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter
public class VidsonetException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String,Object> errorDetails;

    public VidsonetException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errorDetails = null;

    }
    public VidsonetException(ErrorCode errorCode, String message, Map<String, Object> errorDetails) {
        super(message);
        this.errorCode = errorCode;
        this.errorDetails = errorDetails;
    }


    public VidsonetException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorDetails = null;
    }

    public VidsonetException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.errorDetails = null;
    }

}