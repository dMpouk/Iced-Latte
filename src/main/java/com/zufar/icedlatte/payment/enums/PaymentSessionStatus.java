package com.zufar.icedlatte.payment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentSessionStatus {

    PAYMENT_ACTION_IS_REQUIRED("no payment attempt",
            "Payment action is required."),

    PAYMENT_IS_EXPIRED("checkout.session.expired",
            "Checkout Session is expired."),

    PAYMENT_IS_SUCCEEDED("checkout.session.completed",
            "Payment has succeeded.");

    private final String status;
    private final String description;
}

