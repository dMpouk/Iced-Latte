package com.zufar.icedlatte.order.api;

import com.zufar.icedlatte.cart.api.ShoppingCartProvider;
import com.zufar.icedlatte.openapi.dto.OrderStatus;
import com.zufar.icedlatte.openapi.dto.ShoppingCartDto;
import com.zufar.icedlatte.order.converter.OrderDtoConverter;
import com.zufar.icedlatte.order.entity.Order;
import com.zufar.icedlatte.order.entity.OrderItem;
import com.zufar.icedlatte.user.entity.Address;
import com.zufar.icedlatte.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEntityCreator {

    private final OrderDtoConverter orderDtoConverter;
    private final ShoppingCartProvider shoppingCartProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public Order create(UserEntity user) {
        ShoppingCartDto shoppingCartDto = shoppingCartProvider.getByUserIdOrThrow(user.getId());

        Address address = user.getAddress();

        var order = Order.builder()
                .id(UUID.randomUUID())
                .userId(user.getId())
                .status(OrderStatus.CREATED)
                .deliveryAddress(address)
                .itemsQuantity(shoppingCartDto.getItemsQuantity())
                .itemsTotalPrice(shoppingCartDto.getItemsTotalPrice())
                .build();
        List<OrderItem> shoppingOrderItems = shoppingCartDto.getItems().stream()
                .map(orderDtoConverter::toOrderItem)
                .peek(item -> item.setOrderId(order.getId()))
                .toList();
        order.setItems(shoppingOrderItems);
        return order;
    }
}
