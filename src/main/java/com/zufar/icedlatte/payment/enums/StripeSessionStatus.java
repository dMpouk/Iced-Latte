package com.zufar.icedlatte.payment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Stripe Webhook sends <a href="https://docs.stripe.com/api/events/types">various events</a>,
 * So far we're interested only in "checkout.session.expired" and "checkout.session.completed"
 */
@Getter
@RequiredArgsConstructor
public enum StripeSessionStatus {

    PAYMENT_ACTION_IS_REQUIRED("no payment attempt",
            "Payment action is required."),

    SESSION_IS_EXPIRED("checkout.session.expired",
            "Checkout Session is expired."),

    SESSION_IS_COMPLETED("checkout.session.completed",
            "Payment has succeeded.");

    public static class Constants {
        public static final String SESSION_IS_EXPIRED = "checkout.session.expired";
        public static final String SESSION_IS_COMPLETED = "checkout.session.completed";
    }
    private final String status;
    private final String description;
}

