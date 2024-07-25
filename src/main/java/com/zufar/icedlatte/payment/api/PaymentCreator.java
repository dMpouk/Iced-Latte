package com.zufar.icedlatte.payment.api;

import com.stripe.model.checkout.Session;
import com.zufar.icedlatte.order.entity.Order;
import com.zufar.icedlatte.payment.entity.Payment;
import com.zufar.icedlatte.payment.enums.StripeSessionStatus;
import com.zufar.icedlatte.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentCreator {

    private final PaymentRepository paymentRepository;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void createPayment(final Order order,
                              final Session session) {
        var payment = Payment.builder()
                .orderId(order.getId())
                .sessionId(session.getId())
                .status(StripeSessionStatus.PAYMENT_ACTION_IS_REQUIRED)
                .build();
        paymentRepository.save(payment);
    }
}
