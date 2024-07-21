package com.zufar.icedlatte.payment.exception;

import java.util.UUID;

public class StripeSessionCreationException extends RuntimeException {
    public StripeSessionCreationException(String message, UUID orderId) {
        super(String.format("Error creating Stripe session for order %s. %s", orderId, message));
    }
}
