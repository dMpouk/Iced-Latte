package com.zufar.icedlatte.payment.api;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.zufar.icedlatte.order.entity.Order;
import com.zufar.icedlatte.payment.converter.StripeSessionLineItemListConverter;
import com.zufar.icedlatte.payment.dto.PaymentSessionStatus;
import com.zufar.icedlatte.payment.exception.StripeSessionCreationException;
import com.zufar.icedlatte.payment.exception.StripeSessionRetrievalException;
import com.zufar.icedlatte.user.api.SingleUserProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Service
public class StripeSessionProvider {

    private static final String RETURN_URI = "/orders?sessionId={CHECKOUT_SESSION_ID}";

    private final SingleUserProvider singleUserProvider;
    private final StripeShippingOptionsProvider stripeShippingOptionsProvider;
    private final StripeSessionLineItemListConverter stripeSessionLineItemListConverter;

    public Session createSession(final Order order,
                                 final HttpServletRequest request) {
        var user = singleUserProvider.getUserById(order.getUserId());
        SessionCreateParams stripeSessionCreateParams =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setUiMode(SessionCreateParams.UiMode.EMBEDDED)
                        .setCustomerEmail(user.getEmail())
                        .setReturnUrl(getReturnUrl(request))
                        .addAllLineItem(stripeSessionLineItemListConverter.toLineItems(order.getItems()))
                        .addAllShippingOption(stripeShippingOptionsProvider.getShippingOptions())
                        .setAutomaticTax(
                                SessionCreateParams.AutomaticTax.builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();
        try {
            return Session.create(stripeSessionCreateParams);
        } catch (StripeException e) {
            throw new StripeSessionCreationException(e.getMessage(), order.getId());
        }
    }

    public String getReturnUrl(final HttpServletRequest request) {
        var url = UriComponentsBuilder.newInstance()
                .scheme(request.getScheme())
                .host(request.getHeader(HttpHeaders.HOST))
                .path(RETURN_URI)
                .build();
        return url.toUriString();
    }

    public PaymentSessionStatus retrieveSession(String sessionId) throws StripeSessionRetrievalException {
        Session session;
        try {
            session = Session.retrieve(sessionId);
        } catch (StripeException e) {
            throw new StripeSessionRetrievalException(e.getMessage(), sessionId);
        }
        return new PaymentSessionStatus(session.getStatus(), session.getCustomerEmail());
    }
}
