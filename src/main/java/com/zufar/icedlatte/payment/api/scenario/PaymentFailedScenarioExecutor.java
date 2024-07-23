package com.zufar.icedlatte.payment.api.scenario;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.zufar.icedlatte.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

import static com.zufar.icedlatte.payment.enums.PaymentSessionStatus.PAYMENT_IS_EXPIRED;

/**
 * This class is responsible for handling the fail scenario and updating
 * in database record of payment, with the relevant status and description
 * */

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentFailedScenarioExecutor implements PaymentScenarioExecutor {

    private final PaymentRepository paymentRepository;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void execute(Session stripeSession) {
        log.info("Handle payment scenario method: start of handling session: {} by failed scenario.", stripeSession.getId());
        paymentRepository.updateStatusAndDescriptionInPayment(stripeSession.getId(), PAYMENT_IS_EXPIRED.getStatus(), PAYMENT_IS_EXPIRED.getDescription());
        log.info("Handle payment scenario method: finish of handling session: {} by failed scenario.", stripeSession);
    }

    @Override
    public boolean supports(Event event) {
        return Objects.equals(PAYMENT_IS_EXPIRED.getStatus(), event.getType());
    }
}
