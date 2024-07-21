package com.zufar.icedlatte.payment.exception;

public class StripeSessionRetrievalException extends Throwable {
    public StripeSessionRetrievalException(String message, String sessionId) {
        super(String.format("Error retrieving Stripe session with id %s. %s", sessionId, message));
    }
}
