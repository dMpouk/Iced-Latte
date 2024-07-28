package com.zufar.icedlatte.payment.converter;

import com.stripe.param.checkout.SessionCreateParams;
import com.zufar.icedlatte.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        imports = BigDecimal.class)
public interface StripeSessionLineItemListConverter {

    List<SessionCreateParams.LineItem> toLineItems(List<OrderItem> orderItems);

    @Mapping(target = "priceData.unitAmount", expression = "java(orderItem.getProductPrice().multiply(BigDecimal.valueOf(100)).longValue())")
    @Mapping(target = "priceData.currency", constant = "USD")
    @Mapping(target = "priceData.productData.name", source = "productName")
    @Mapping(target = "quantity", source = "productsQuantity")
    SessionCreateParams.LineItem toLineItem(OrderItem orderItem);
}
