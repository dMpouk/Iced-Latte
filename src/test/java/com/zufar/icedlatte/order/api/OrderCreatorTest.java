package com.zufar.icedlatte.order.api;

import com.zufar.icedlatte.cart.api.ShoppingCartProvider;
import com.zufar.icedlatte.openapi.dto.OrderResponseDto;
import com.zufar.icedlatte.openapi.dto.ShoppingCartDto;
import com.zufar.icedlatte.order.converter.OrderDtoConverter;
import com.zufar.icedlatte.order.entity.Order;
import com.zufar.icedlatte.order.repository.OrderRepository;
import com.zufar.icedlatte.security.api.SecurityPrincipalProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.zufar.icedlatte.order.stub.OrderDtoTestStub.createOrderRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderCreatorTest {

    @InjectMocks
    private OrderCreator orderCreator;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SecurityPrincipalProvider securityPrincipalProvider;

    @Mock
    private OrderDtoConverter orderDtoConverter;

    @Mock
    private  ShoppingCartProvider shoppingCartProvider;

    @Test
    @DisplayName("Create order should return the OrderResponseDto")
    void shouldCreateOrderAndReturnOrderResponseDto() {
        UUID userId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();
        var orderRequest = createOrderRequestDto();
        var addressDto = orderRequest.getAddress();
        var orderEntity = new Order();
        var orderResponse = new OrderResponseDto();
        var shoppingCartDto = new ShoppingCartDto();

        when(securityPrincipalProvider.getUserId()).thenReturn(userId);
        when(shoppingCartProvider.getByUserIdOrThrow(userId)).thenReturn(shoppingCartDto);
        when(addressProvider.addNewAddress(addressDto)).thenReturn(addressId);
        when(orderRepository.saveAndFlush(orderEntity)).thenReturn(orderEntity);
        when(orderDtoConverter.toResponseDto(orderEntity)).thenReturn(orderResponse);

        OrderResponseDto result = orderCreator.createOrder(orderRequest);

        assertEquals(result, orderResponse);

        verify(securityPrincipalProvider, times(1)).getUserId();
        verify(shoppingCartProvider, times(1)).getByUserIdOrThrow(userId);
        verify(addressProvider, times(1)).addNewAddress(addressDto);
        verify(orderRepository, times(1)).saveAndFlush(orderEntity);
        verify(orderDtoConverter, times(1)).toResponseDto(orderEntity);
    }
}
