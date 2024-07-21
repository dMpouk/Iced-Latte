package com.zufar.icedlatte.payment.exception;

import java.util.UUID;

public class OrderAlreadyPaidException extends RuntimeException {
    private final UUID orderId;

    public OrderAlreadyPaidException(UUID orderId) {
        super(String.format("Order '%s' is already paid.", orderId));
        this.orderId = orderId;
    }
}
