package com.zufar.icedlatte.payment.api.scenario;

import com.stripe.model.checkout.Session;
import com.zufar.icedlatte.email.sender.PaymentEmailConfirmation;
import com.zufar.icedlatte.order.repository.OrderRepository;
import com.zufar.icedlatte.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.zufar.icedlatte.payment.enums.StripeSessionStatus.SESSION_IS_EXPIRED;

@Slf4j
@RequiredArgsConstructor
@Service
public class SessionExpiredScenario {

    private final PaymentRepository paymentRepository;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void handle(Session stripeSession) {
        log.info("Checkout session expired {}, updating payment table.", stripeSession.getId());
        paymentRepository.updateStatusAndDescriptionInPayment(stripeSession.getId(), SESSION_IS_EXPIRED.getStatus(), SESSION_IS_EXPIRED.getDescription());
    }
}
