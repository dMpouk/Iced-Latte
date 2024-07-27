package com.zufar.icedlatte.payment.api;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionExpireParams;
import com.zufar.icedlatte.openapi.dto.OrderStatus;
import com.zufar.icedlatte.openapi.dto.SessionWithClientSecretDto;
import com.zufar.icedlatte.order.api.OrderProvider;
import com.zufar.icedlatte.payment.api.session.StripeSessionProvider;
import com.zufar.icedlatte.payment.config.StripeConfiguration;
import com.zufar.icedlatte.payment.exception.OrderAlreadyPaidException;
import com.zufar.icedlatte.security.api.SecurityPrincipalProvider;
import jakarta.annotation.PostConstruct;
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

    private final StripeConfiguration stripeConfiguration;
    private final StripeSessionProvider stripeSessionProvider;
    private final SecurityPrincipalProvider securityPrincipalProvider;
    private final PaymentCreator paymentCreator;
    private final OrderProvider orderProvider;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @PostConstruct
    private void initStripe() {
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

        paymentCreator.createPayment(order, stripeSession);

        SessionWithClientSecretDto sessionDto = new SessionWithClientSecretDto();
        sessionDto.setSessionId(stripeSession.getId());
        sessionDto.setClientSecret(stripeSession.getClientSecret());

        return sessionDto;
    }
}
