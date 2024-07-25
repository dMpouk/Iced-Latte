package com.zufar.icedlatte.payment.api.scenario;

import com.stripe.model.checkout.Session;
import com.zufar.icedlatte.email.sender.PaymentEmailConfirmation;
import com.zufar.icedlatte.openapi.dto.OrderStatus;
import com.zufar.icedlatte.order.repository.OrderRepository;
import com.zufar.icedlatte.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.zufar.icedlatte.payment.enums.StripeSessionStatus.SESSION_IS_SUCCEEDED;

@Slf4j
@RequiredArgsConstructor
@Service
public class SessionCompletedScenario {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentEmailConfirmation paymentEmailConfirmation;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void handle(Session session) {
        log.info("Checkout session completed {}, updating payment and order tables.", session.getId());
        paymentRepository.updateStatusAndDescriptionInPayment(session.getId(), SESSION_IS_SUCCEEDED.toString(), SESSION_IS_SUCCEEDED.getDescription());
        var paymentEntity = paymentRepository.findBySessionId(session.getId());
        // TODO: update shipping option?
        orderRepository.updateOrderStatus(paymentEntity.getOrderId(), OrderStatus.PAID.toString());
        paymentEmailConfirmation.send(session);
        log.info("Confirmation email was sent.");
    }
}
