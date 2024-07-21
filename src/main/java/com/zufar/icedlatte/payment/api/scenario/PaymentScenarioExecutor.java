package com.zufar.icedlatte.payment.api.scenario;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;

public interface PaymentScenarioExecutor {

    void execute(final Session session);

    boolean supports(final Event event);

}
