package com.zufar.icedlatte.payment.api;

import com.stripe.model.checkout.Session;
import com.zufar.icedlatte.openapi.dto.SessionWithClientSecretDto;
import com.zufar.icedlatte.payment.exception.OrderAlreadyPaidException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentProcessor {

    private final StripeSessionCreator stripeSessionCreator;

    public SessionWithClientSecretDto processPayment(final HttpServletRequest request) throws OrderAlreadyPaidException {
        Session stripeSession = stripeSessionCreator.createSession(request);

        SessionWithClientSecretDto sessionDto = new SessionWithClientSecretDto();
        sessionDto.setSessionId(stripeSession.getId());
        sessionDto.setClientSecret(stripeSession.getClientSecret());

        return sessionDto;
    }
}
