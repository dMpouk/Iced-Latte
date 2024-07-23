package com.zufar.icedlatte.payment.exception;

import java.util.UUID;

public class StripeSessionCreationException extends RuntimeException {

    public StripeSessionCreationException(final String message,
                                         final UUID orderId) {
        super(String.format("Error creating Stripe session for order with id = '%s'. Error message = '%s'", orderId, message));
    }
}
