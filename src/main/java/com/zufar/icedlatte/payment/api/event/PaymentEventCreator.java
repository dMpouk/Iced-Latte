package com.zufar.icedlatte.payment.api.event;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.zufar.icedlatte.payment.exception.PaymentEventProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * This class is responsible for payment event (stripe object) creation.
 * */

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentEventCreator {

    @Value("${stripe.webhook-secret}")
    private String webHookSecret;

    public Event createPaymentEvent(String paymentPayload, String stripeSignatureHeader) {
        try {
            return Webhook.constructEvent(paymentPayload, stripeSignatureHeader, webHookSecret);
        } catch (SignatureVerificationException ex) {
            log.error("Error during payment event creating", ex);
            throw new PaymentEventProcessingException(stripeSignatureHeader);
        }
    }
}
