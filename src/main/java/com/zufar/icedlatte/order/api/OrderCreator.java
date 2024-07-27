package com.zufar.icedlatte.order.api;

import com.zufar.icedlatte.cart.api.ShoppingCartManager;
import com.zufar.icedlatte.openapi.dto.CreateNewOrderRequestDto;
import com.zufar.icedlatte.openapi.dto.OrderResponseDto;
import com.zufar.icedlatte.order.converter.OrderDtoConverter;
import com.zufar.icedlatte.order.converter.OrderItemDtoConverter;
import com.zufar.icedlatte.order.entity.Order;
import com.zufar.icedlatte.order.repository.OrderRepository;
import com.zufar.icedlatte.security.api.SecurityPrincipalProvider;
import com.zufar.icedlatte.user.api.AddressProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

import static java.util.stream.Collectors.toCollection;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCreator {

    private final OrderRepository orderRepository;
    private final ShoppingCartManager shoppingCartManager;
    private final OrderDtoConverter orderDtoConverter;
    private final SecurityPrincipalProvider securityPrincipalProvider;
    private final AddressProvider addressProvider;
    private final OrderItemDtoConverter orderItemDtoConverter;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public OrderResponseDto createOrder(final CreateNewOrderRequestDto orderRequest) {
        UUID userId = securityPrincipalProvider.getUserId();
        var shoppingCart = shoppingCartManager.getByUserIdOrThrow(userId);
        UUID shoppingCartId = shoppingCart.getId();

        var addressId = addressProvider.addNewAddress(orderRequest.getAddress());
        var newOrderEntity = orderDtoConverter.toOrder(shoppingCart, addressId);
        Order savedOrderEntity = orderRepository.saveAndFlush(newOrderEntity);
        // FIXME: code smell
        savedOrderEntity.setItems(shoppingCart.getItems().stream()
                .map(orderItemDtoConverter::toOrderItem).collect(toCollection(ArrayList::new)));
        savedOrderEntity.getItems().forEach(item -> item.setOrderId(savedOrderEntity.getId()));
        // FIXME: code smell
        orderRepository.save(savedOrderEntity);
        log.info("New order with id = '{}' was created and saved to database.", savedOrderEntity.getId());

        shoppingCartManager.deleteById(shoppingCartId);
        log.info("Deleted the shopping cart with id = '{}'", shoppingCartId);

        return orderDtoConverter.toResponseDto(savedOrderEntity);
    }
}
