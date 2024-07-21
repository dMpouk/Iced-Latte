package com.zufar.icedlatte.payment.api.event;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.web.exchanges.HttpExchangesAutoConfiguration;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * This class is responsible for processing payment event to transfer it to the responsibility area
 * of class that is engaged in catching event types.
 * */

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentEventProcessor {

    private final PaymentEventCreator paymentEventCreator;
    private final PaymentEventParser paymentEventParser;
    private final PaymentEventHandler paymentEventHandler;

    public void processPaymentEvent(String paymentPayload, String stripeSignatureHeader) {
        log.info("Process payment event: start payment event processing");
        Event event = paymentEventCreator.createPaymentEvent(paymentPayload, stripeSignatureHeader);
        var session = paymentEventParser.parseEventToSession(event);
        paymentEventHandler.handlePaymentEvent(event, session);
        log.info("Process payment event: event successfully processed");
    }

}
