package com.zufar.icedlatte.order.api;

import com.zufar.icedlatte.openapi.dto.OrderRequestDto;
import com.zufar.icedlatte.openapi.dto.OrderStatus;
import com.zufar.icedlatte.openapi.dto.ProductInfoDto;
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

    public Order createNewOrder(final OrderRequestDto requestDto,
                                final ShoppingCartDto shoppingCartDto) {
        var order = Order.builder()
                .userId(shoppingCartDto.getUserId())
                .itemsQuantity(shoppingCartDto.getItemsQuantity())
                .status(OrderStatus.CREATED)
                .line(requestDto.getLine())
                .city(requestDto.getCity())
                .country(requestDto.getCountry())
                .postcode(requestDto.getPostCode())
                .deliveryCost(DELIVERY_COST)
                .taxCost(TAX_COST)
                .build();

        var orderItems = shoppingCartDto.getItems().stream()
                .map(shoppingCartItem -> {
                    ProductInfoDto productInfo = shoppingCartItem.getProductInfo();
                    return OrderItem.builder()
                                    .order(order)
                                    .price(productInfo.getPrice())
                                    .productName(productInfo.getName())
                                    .productQuantity(shoppingCartItem.getProductQuantity())
                                    .productId(productInfo.getId())
                                    .build();
                        }
                )
                .toList();
        order.setItems(orderItems);
        return order;
    }
}
