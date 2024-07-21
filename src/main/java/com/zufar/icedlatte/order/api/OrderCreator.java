package com.zufar.icedlatte.order.api;

import com.zufar.icedlatte.cart.api.ShoppingCartManager;
import com.zufar.icedlatte.openapi.dto.OrderRequestDto;
import com.zufar.icedlatte.openapi.dto.OrderResponseDto;
import com.zufar.icedlatte.openapi.dto.ShoppingCartDto;
import com.zufar.icedlatte.order.converter.OrderDtoConverter;
import com.zufar.icedlatte.order.entity.Order;
import com.zufar.icedlatte.order.repository.OrderRepository;
import com.zufar.icedlatte.security.api.SecurityPrincipalProvider;
import com.zufar.icedlatte.user.api.SingleUserProvider;
import com.zufar.icedlatte.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCreator {

    private final OrderRepository orderRepository;
    private final ShoppingCartManager shoppingCartManager;
    private final OrderDtoConverter orderDtoConverter;
    private final SecurityPrincipalProvider securityPrincipalProvider;
    private final OrderEntityCreator orderEntityCreator;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public OrderResponseDto createOrder(final OrderRequestDto orderRequest) {
        UUID userId = securityPrincipalProvider.getUserId();
        var cart = shoppingCartManager.getShoppingCartByUserId(userId);
        var order = orderEntityCreator.createNewOrder(orderRequest, cart);
        orderRepository.saveAndFlush(order);
        log.info("New order was created and saved to database.");
        shoppingCartManager.deleteById(cart.getId());
        log.info("Deleted shopping cart with id {}", cart.getId());
        return orderDtoConverter.toResponseDto(order);
    }
}
