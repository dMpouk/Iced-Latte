package com.zufar.icedlatte.payment.api.event;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.zufar.icedlatte.payment.api.scenario.PaymentScenarioExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * This class is responsible for catching payment event type, comparing it with existing
 * payment statuses and based on their correspondence, calling the desired scenario handler.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentEventHandler {

    private final List<PaymentScenarioExecutor> stripePaymentScenarioExecutors;

    public void handlePaymentEvent(final Event stripePaymentEvent,
                                   final Session stripeSession) {

        if (!Objects.nonNull(stripeSession) || !Objects.nonNull(stripePaymentEvent)) {
            return;
        }

        log.info("Handle Stripe payment event method: start of handling Stripe payment event");

        stripePaymentScenarioExecutors.stream()
                .filter(executor -> executor.supports(stripePaymentEvent))
                .findFirst()
                .ifPresent(executor -> executor.execute(stripeSession));

        log.info("Handle Stripe payment event method: finish of handling Stripe payment event");
    }
}
