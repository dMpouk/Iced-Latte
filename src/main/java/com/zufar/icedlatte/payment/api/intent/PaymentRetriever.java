package com.zufar.icedlatte.payment.api.intent;

import com.zufar.icedlatte.cart.exception.ShoppingCartNotFoundException;
import com.zufar.icedlatte.order.repository.OrderRepository;
import com.zufar.icedlatte.payment.converter.PaymentConverter;
import com.zufar.icedlatte.openapi.dto.ProcessedPaymentDetailsDto;
import com.zufar.icedlatte.payment.exception.PaymentNotFoundException;
import com.zufar.icedlatte.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * This class is responsible for retrieving relevant payment details from database
 * */

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentRetriever {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentConverter paymentConverter;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public ProcessedPaymentDetailsDto getPaymentDetails(final String paymentId) {
        log.info("Get payment details: starting: payment details retrieve by payment id = {}.", paymentId);

        return paymentRepository.findById(paymentId)
                .map(payment -> {
                    UUID orderId = payment.getOrderId();
                    return orderRepository.findById(orderId)
                            .map(order -> paymentConverter.toDto(payment, order.getItems()))
                            .orElseThrow(() -> new ShoppingCartNotFoundException(orderId));
                })
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
    }
}
