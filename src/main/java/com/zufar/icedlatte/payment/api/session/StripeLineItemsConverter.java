package com.zufar.icedlatte.payment.api.session;

import com.stripe.param.checkout.SessionCreateParams;
import com.zufar.icedlatte.order.entity.Order;
import com.zufar.icedlatte.order.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StripeLineItemsConverter {

    public List<SessionCreateParams.LineItem> getLineItems(Order order) {
        var result = new ArrayList<SessionCreateParams.LineItem>();
        for (OrderItem item : order.getItems()) {
            var quantity = item.getProductQuantity();
            // convert to cents
            var unitAmount = item.getPrice().multiply(BigDecimal.valueOf(100)).longValue();
            var productName = item.getProductName();
            var productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName(productName)
                    .build();
            var priceData = SessionCreateParams.LineItem.PriceData.builder()
                    .setUnitAmount(unitAmount)
                    .setCurrency("USD")
                    .setProductData(productData)
                    .build();
            var lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity((long) quantity)
                    .setPriceData(priceData)
                    .build();
            result.add(lineItem);
        }
        return result;
    }
}
