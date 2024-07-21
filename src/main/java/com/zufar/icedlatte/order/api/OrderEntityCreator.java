package com.zufar.icedlatte.order.api;

import com.zufar.icedlatte.openapi.dto.OrderRequestDto;
import com.zufar.icedlatte.openapi.dto.OrderStatus;
import com.zufar.icedlatte.openapi.dto.ShoppingCartDto;
import com.zufar.icedlatte.order.entity.Order;
import com.zufar.icedlatte.order.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEntityCreator {

    private static final BigDecimal DELIVERY_COST = BigDecimal.ONE; // FIXME: make it dynamic
    private static final BigDecimal TAX_COST = BigDecimal.ONE; // FIXME: make it dynamic

    public Order createNewOrder(OrderRequestDto requestDto, ShoppingCartDto cartDto) {
        var order = Order.builder()
                .userId(cartDto.getUserId())
                .itemsQuantity(cartDto.getItemsQuantity())
                .status(OrderStatus.CREATED)
                .line(requestDto.getLine())
                .city(requestDto.getCity())
                .country(requestDto.getCountry())
                .postcode(requestDto.getPostCode())
                .deliveryCost(DELIVERY_COST)
                .taxCost(TAX_COST)
                .build();
        var items = cartDto.getItems().stream().map(
                        cartItem -> OrderItem.builder()
                                .order(order)
                                .price(cartItem.getProductInfo().getPrice())
                                .productName(cartItem.getProductInfo().getName())
                                .productQuantity(cartItem.getProductQuantity())
                                .productId(cartItem.getProductInfo().getId())
                                .build())
                .toList();
        order.setItems(items);
        return order;
    }
}
