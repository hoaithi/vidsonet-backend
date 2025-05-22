package com.hoaithidev.vidsonet_backend.service;

import com.hoaithidev.vidsonet_backend.dto.payment.CreatePaymentRequest;
import com.hoaithidev.vidsonet_backend.dto.payment.CreatePaymentResponse;
import com.hoaithidev.vidsonet_backend.dto.payment.PaymentCaptureResponse;
import com.hoaithidev.vidsonet_backend.model.Membership;

public interface PaymentService {

    /**
     * Creates a PayPal payment for membership
     * @param membershipTierId The membership tier ID
     * @param userId The user ID
     * @return CreatePaymentResponse containing the approval URL and payment details
     */
    CreatePaymentResponse createPayment(Long membershipTierId, Long userId);

    /**
     * Captures the payment after user approval
     * @param paymentId The PayPal payment ID
     * @param payerId The PayPal payer ID
     * @param userId The user ID
     * @return The captured payment information
     */
    PaymentCaptureResponse capturePayment(String paymentId, String payerId, Long userId);

    /**
     * Confirms the membership after successful payment
     * @param membershipId The membership ID to confirm
     * @param transactionId The PayPal transaction ID
     * @return The activated membership
     */
    Membership confirmMembership(Long membershipId, String transactionId);

    /**
     * Cancels a membership payment
     * @param paymentId The PayPal payment ID
     */
    void cancelPayment(String paymentId);
}