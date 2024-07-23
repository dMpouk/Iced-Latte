package com.zufar.icedlatte.payment.api.session;

import com.stripe.param.checkout.SessionCreateParams;
import com.zufar.icedlatte.order.entity.Order;
import com.zufar.icedlatte.order.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StripeSessionLineItemsConverter {

    private static final String DEFAULT_CURRENCY = "USD";

    public List<SessionCreateParams.LineItem> convertToLineItems(Order order) {
        return order.getItems().stream()
                .map(this::convertToLineItem)
                .toList();
    }

    private SessionCreateParams.LineItem convertToLineItem(OrderItem orderItem) {
        var unitAmount = orderItem.getPrice().multiply(BigDecimal.valueOf(100)).longValue();

        var productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(orderItem.getProductName())
                .build();

        var priceData = SessionCreateParams.LineItem.PriceData.builder()
                .setUnitAmount(unitAmount)
                .setCurrency(DEFAULT_CURRENCY)
                .setProductData(productData)
                .build();

        return SessionCreateParams.LineItem.builder()
                .setQuantity((long) orderItem.getProductQuantity())
                .setPriceData(priceData)
                .build();
    }
}
