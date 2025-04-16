package com.hoaithidev.vidsonet_backend.exception;

public class DuplicateResourceException extends VidsonetException {
        public DuplicateResourceException(String message) {
        super(ErrorCode.DUPLICATE_RESOURCE, message);
    }
}
