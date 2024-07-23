package com.zufar.icedlatte.order.api;

import com.zufar.icedlatte.cart.api.ShoppingCartManager;
import com.zufar.icedlatte.openapi.dto.OrderRequestDto;
import com.zufar.icedlatte.openapi.dto.OrderResponseDto;
import com.zufar.icedlatte.order.converter.OrderDtoConverter;
import com.zufar.icedlatte.order.entity.Order;
import com.zufar.icedlatte.order.repository.OrderRepository;
import com.zufar.icedlatte.security.api.SecurityPrincipalProvider;
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
        var shoppingCart = shoppingCartManager.getShoppingCartByUserId(userId);
        UUID shoppingCartId = shoppingCart.getId();

        var newOrderEntity = orderEntityCreator.createNewOrder(orderRequest, shoppingCart);
        Order savedOrderEntity = orderRepository.saveAndFlush(newOrderEntity);
        log.info("New order with id = '{}' was created and saved to database.", savedOrderEntity.getId());

        shoppingCartManager.deleteById(shoppingCartId);
        log.info("Deleted the shopping cart with id = '{}'", shoppingCartId);

        return orderDtoConverter.toResponseDto(savedOrderEntity);
    }
}
