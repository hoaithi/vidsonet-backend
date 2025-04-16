package com.hoaithidev.vidsonet_backend.exception;


public class ResourceNotFoundException extends VidsonetException {
    public ResourceNotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message);
    }
}