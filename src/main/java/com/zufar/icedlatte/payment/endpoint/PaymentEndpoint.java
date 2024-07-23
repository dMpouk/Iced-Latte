package com.zufar.icedlatte.payment.endpoint;

import com.zufar.icedlatte.openapi.dto.ProcessedPaymentDetailsDto;
import com.zufar.icedlatte.openapi.dto.SessionWithClientSecretDto;
import com.zufar.icedlatte.payment.api.event.PaymentEventProcessor;
import com.zufar.icedlatte.payment.api.intent.PaymentProcessor;
import com.zufar.icedlatte.payment.api.intent.PaymentRetriever;
import com.zufar.icedlatte.payment.api.session.StripeSessionProvider;
import com.zufar.icedlatte.payment.dto.PaymentSessionStatus;
import com.zufar.icedlatte.payment.exception.OrderAlreadyPaidException;
import com.zufar.icedlatte.payment.exception.StripeSessionRetrievalException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(PaymentEndpoint.PAYMENT_URL)
public class PaymentEndpoint implements com.zufar.icedlatte.openapi.payment.api.PaymentApi {

    // stripe listen --forward-to localhost:80/backend/api/v1/payment/stripe/webhook
    public static final String PAYMENT_URL = "/api/v1/payment";

    private final PaymentRetriever paymentRetriever;
    private final PaymentProcessor paymentProcessor;
    private final PaymentEventProcessor paymentEventProcessor;
    private final StripeSessionProvider stripeSessionProvider;

    // TEST CARD: 4242424242424242
    @GetMapping
    public ResponseEntity<SessionWithClientSecretDto> processPayment(@RequestParam final UUID orderId,
                                                                     final HttpServletRequest request) throws OrderAlreadyPaidException {
        log.info("Received request to process payment for the order with id = '{}'", orderId);
        var paymentSession = paymentProcessor.processPayment(orderId, request);
        log.info("Payment session was created successfully for the order with id = '{}'", orderId);
        return ResponseEntity.ok().body(paymentSession);
    }

    @PostMapping("/stripe/webhook")
    public ResponseEntity<Void> processStripeWebhook(@RequestHeader("Stripe-Signature") final String stripeSignatureHeader,
                                                     @RequestBody final String paymentPayload){
        log.info("Received Stripe payment webhook");
        paymentEventProcessor.processPaymentEvent(paymentPayload, stripeSignatureHeader);
        log.info("Finished processing Stripe payment webhook");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/status")
    public ResponseEntity<PaymentSessionStatus> getPaymentStatus(@RequestParam final String sessionID) throws StripeSessionRetrievalException {
        log.info("Received request to get payment status, session id {}", sessionID);
        var sessionStatus = stripeSessionProvider.retrieveSession(sessionID);
        log.info("Finished processing payment status retrieval");
        return ResponseEntity.ok().body(sessionStatus);
    }

    // TODO: re-work this method, do we need that?
    @GetMapping("/{paymentId}")
    public ResponseEntity<ProcessedPaymentDetailsDto> getPaymentDetails(@PathVariable final String paymentId) {
        ProcessedPaymentDetailsDto retrievedPayment = paymentRetriever.getPaymentDetails(paymentId);
        return ResponseEntity.ok().body(retrievedPayment);
    }
}