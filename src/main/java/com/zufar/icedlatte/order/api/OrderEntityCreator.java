package com.zufar.icedlatte.order.api;

import com.zufar.icedlatte.cart.api.ShoppingCartProvider;
import com.zufar.icedlatte.openapi.dto.AddressDto;
import com.zufar.icedlatte.openapi.dto.OrderStatus;
import com.zufar.icedlatte.openapi.dto.ShoppingCartDto;
import com.zufar.icedlatte.openapi.dto.UserDto;
import com.zufar.icedlatte.order.converter.OrderDtoConverter;
import com.zufar.icedlatte.order.entity.Order;
import com.zufar.icedlatte.order.entity.OrderItem;
import com.zufar.icedlatte.user.converter.AddressDtoConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEntityCreator {

    private final OrderDtoConverter orderDtoConverter;
    private final AddressDtoConverter addressDtoConverter;
    private final ShoppingCartProvider shoppingCartProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public Order create(UserDto userDto) {
        ShoppingCartDto shoppingCartDto = shoppingCartProvider.getByUserIdOrThrow(userDto.getId());

        AddressDto address = userDto.getAddress();

        List<OrderItem> shoppingOrderItems = shoppingCartDto.getItems().stream()
                .map(orderDtoConverter::toOrderItem)
                .toList();

        return Order.builder()
                .userId(userDto.getId())
                .status(OrderStatus.CREATED)
                .items(shoppingOrderItems)
                .deliveryAddress(addressDtoConverter.toEntity(address))
                .itemsQuantity(shoppingOrderItems.size())
                .itemsTotalPrice(shoppingCartDto.getItemsTotalPrice())
                .build();
    }
}
