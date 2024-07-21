package com.zufar.icedlatte.payment.api.event;

import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
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

    private final List<PaymentScenarioExecutor> executors;

    public void handlePaymentEvent(Event event, Session session) {
        if (Objects.nonNull(session) && Objects.nonNull(event)) {
            log.info("Handle payment event method: start of handling payment event");

            executors.stream()
                    .filter(executor -> executor.supports(event))
                    .findFirst()
                    .ifPresent(executor -> executor.execute(session));

            log.info("Handle payment event method: finish of handling payment event");
        }
    }
}
