package com.zufar.icedlatte.payment.api;

import com.stripe.Stripe;
import com.zufar.icedlatte.openapi.dto.OrderStatus;
import com.zufar.icedlatte.openapi.dto.SessionWithClientSecretDto;
import com.zufar.icedlatte.order.api.OrderProvider;
import com.zufar.icedlatte.payment.entity.Payment;
import com.zufar.icedlatte.payment.enums.StripeSessionStatus;
import com.zufar.icedlatte.payment.exception.OrderAlreadyPaidException;
import com.zufar.icedlatte.payment.repository.PaymentRepository;
import com.zufar.icedlatte.security.api.SecurityPrincipalProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentProcessor {

    private final StripeSessionProvider stripeSessionProvider;
    private final SecurityPrincipalProvider securityPrincipalProvider;
    private final OrderProvider orderProvider;
    private final PaymentRepository paymentRepository;

    @Value("${stripe.secret-key}")
    public void setStripeSecretKey(String stripeSecretKey) {
        Stripe.apiKey = stripeSecretKey;
    }

    public SessionWithClientSecretDto processPayment(final UUID orderId,
                                                     final HttpServletRequest request) throws OrderAlreadyPaidException {
        var userId = securityPrincipalProvider.getUserId();
        var order = orderProvider.getOrderEntityById(userId, orderId);

        if (OrderStatus.PAID == order.getStatus()) {
            throw new OrderAlreadyPaidException(orderId);
        }
        // TODO: should we check if there is already created stripeSession?
        var stripeSession = stripeSessionProvider.createSession(order, request);

        var payment = Payment.builder()
                .orderId(order.getId())
                .sessionId(stripeSession.getId())
                .status(StripeSessionStatus.PAYMENT_ACTION_IS_REQUIRED)
                .build();
        paymentRepository.save(payment);

        var sessionDto = new SessionWithClientSecretDto();
        sessionDto.setSessionId(stripeSession.getId());
        sessionDto.setClientSecret(stripeSession.getClientSecret());

        return sessionDto;
    }
}
