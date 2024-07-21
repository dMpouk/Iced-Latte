package com.zufar.icedlatte.payment.api.event;

import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * This class is responsible for parsing payment event to session object.
 */

@Slf4j
@Service
public class PaymentEventParser {

    public Session parseEventToSession(final Event event) {
        log.info("Parse event object {}", event.getType());
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        var sessionOptional = dataObjectDeserializer.getObject()
                .filter(obj -> obj instanceof Session)
                .map(Session.class::cast);
        if (sessionOptional.isEmpty()) {
            log.info("Event {} is not related to session, skipping", event.getType());
            return null;
        }
        return sessionOptional.get();
    }
}
