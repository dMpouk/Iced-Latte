package com.zufar.icedlatte.order.api;

import com.zufar.icedlatte.cart.api.ShoppingCartProvider;
import com.zufar.icedlatte.cart.repository.ShoppingCartRepository;
import com.zufar.icedlatte.openapi.dto.CreateNewOrderRequestDto;
import com.zufar.icedlatte.openapi.dto.OrderResponseDto;
import com.zufar.icedlatte.openapi.dto.ShoppingCartDto;
import com.zufar.icedlatte.openapi.dto.UserDto;
import com.zufar.icedlatte.order.converter.OrderDtoConverter;
import com.zufar.icedlatte.order.entity.Order;
import com.zufar.icedlatte.order.repository.OrderRepository;
import com.zufar.icedlatte.security.api.SecurityPrincipalProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCreator {

    private final OrderRepository orderRepository;
    private final OrderDtoConverter orderDtoConverter;
    private final SecurityPrincipalProvider securityPrincipalProvider;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartProvider shoppingCartProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public OrderResponseDto createOrder(final CreateNewOrderRequestDto createNewOrderRequest) {
        UserDto userDto = securityPrincipalProvider.get();
        UUID userId = userDto.getId();
        UUID deliveryAddress = userDto.getId();

        ShoppingCartDto shoppingCartDto = shoppingCartProvider.getByUserIdOrThrow(userId);

        Order newOrderEntity = orderDtoConverter.toOrder(shoppingCartDto, deliveryAddress, userId);

        Order savedOrderEntity = orderRepository.saveAndFlush(newOrderEntity);
        log.info("New order with id = '{}' was created and saved to database.", savedOrderEntity.getId());

        shoppingCartRepository.deleteById(shoppingCartDto.getId());
        log.info("Deleted the shopping cart with id = '{}'", shoppingCartDto.getId());

        return orderDtoConverter.toResponseDto(savedOrderEntity);
    }
}
