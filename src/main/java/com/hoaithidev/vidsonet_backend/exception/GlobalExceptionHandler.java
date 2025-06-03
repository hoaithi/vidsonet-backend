package com.hoaithidev.vidsonet_backend.exception;

import com.hoaithidev.vidsonet_backend.dto.user.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(VidsonetException.class)
    public ResponseEntity<ApiResponse<Object>> handleVidsonetException(VidsonetException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ApiResponse.ApiResponseBuilder<Object> responseBuilder = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(ex.getMessage());
        // Add error details if available
        if (ex.getErrorDetails() != null && !ex.getErrorDetails().isEmpty()) {
            responseBuilder.data(ex.getErrorDetails());
        }
        ApiResponse<Object> response = responseBuilder.build();

        return ResponseEntity.status(errorCode.getStatusCode()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ApiResponse.<Map<String, String>>builder()
                .code(ErrorCode.INVALID_REQUEST.getCode())
                .message("Validation failed")
                .data(errors)
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        return ApiResponse.builder()
                .code(ErrorCode.INVALID_REQUEST.getCode())
                .message(ex.getMessage())
                .build();
    }

    // hai lỗi bên dưới xảy ra khi xem chi tiết video
    @ExceptionHandler(ClientAbortException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleConstraintViolationException(ClientAbortException ex) {
        return ApiResponse.builder()
                .code(ErrorCode.INVALID_REQUEST.getCode())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(HttpMessageNotWritableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleConstraintViolationException(HttpMessageNotWritableException ex) {
        return ApiResponse.builder()
                .code(ErrorCode.INVALID_REQUEST.getCode())
                .message(ex.getMessage())
                .build();
    }


    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Object> handleAuthenticationException(AuthenticationException ex) {
        return ApiResponse.builder()
                .code(ErrorCode.UNAUTHORIZED.getCode())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Object> handleAccessDeniedException() {
        return ApiResponse.builder()
                .code(ErrorCode.FORBIDDEN.getCode())
                .message(ErrorCode.FORBIDDEN.getMessage())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Object> handleAllUncaughtException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ApiResponse.builder()
                .code(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
                .message("An unexpected error occurred. Please try again later.")
                .build();
    }

//    @ExceptionHandler(NoHandlerFoundException.class)
//    public ResponseEntity<ApiResponse<Object>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
//        ApiResponse<Object> response = ApiResponse.builder()
//                .code(ErrorCode.RESOURCE_NOT_FOUND.getCode())
//                .message("The requested resource was not found")
//                .build();
//
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//    }

    @ExceptionHandler({NoResourceFoundException.class, ResourceNotFoundException.class})
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(Exception ex) {
        ApiResponse<Object> response = ApiResponse.builder()
                .code(ErrorCode.RESOURCE_NOT_FOUND.getCode())
                .message("The requested resource was not found")
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException ex) {
        ApiResponse<Object> response = ApiResponse.builder()
                .code(ErrorCode.INVALID_CREDENTIALS.getCode())
                .message("Invalid username or password")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


}