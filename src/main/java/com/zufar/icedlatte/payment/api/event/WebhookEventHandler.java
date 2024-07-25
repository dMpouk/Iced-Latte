package com.zufar.icedlatte.payment.api.event;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.zufar.icedlatte.payment.api.scenario.SessionCompletedScenario;
import com.zufar.icedlatte.payment.api.scenario.SessionExpiredScenario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.zufar.icedlatte.payment.enums.StripeSessionStatus.SESSION_IS_EXPIRED;
import static com.zufar.icedlatte.payment.enums.StripeSessionStatus.SESSION_IS_SUCCEEDED;

/**
 * Stripe Webhook sends <a href="https://docs.stripe.com/api/events/types">various events</a>,
 * So far we're interested only in "checkout.session.expired" and "checkout.session.completed"
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class WebhookEventHandler {

    private final SessionExpiredScenario sessionExpiredScenario;
    private final SessionCompletedScenario sessionCompletedScenario;

    public void handlePaymentEvent(final Event stripePaymentEvent,
                                   final Session stripeSession) {

        if (!Objects.nonNull(stripeSession) || !Objects.nonNull(stripePaymentEvent)) {
            return;
        }
        if (Objects.equals(SESSION_IS_SUCCEEDED.getStatus(), stripePaymentEvent.getType())) {
            sessionCompletedScenario.handle(stripeSession);
        } else if (Objects.equals(SESSION_IS_EXPIRED.getStatus(), stripePaymentEvent.getType())) {
            sessionExpiredScenario.handle(stripeSession);
        } else {
            log.info("Unsupported event type, ignoring.");
        }
    }
}
