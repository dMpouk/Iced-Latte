package com.zufar.icedlatte.order.endpoint;

import com.zufar.icedlatte.openapi.dto.CreateNewOrderRequestDto;
import com.zufar.icedlatte.openapi.dto.OrderResponseDto;
import com.zufar.icedlatte.openapi.dto.OrderStatus;
import com.zufar.icedlatte.order.api.OrderCreator;
import com.zufar.icedlatte.order.api.OrderProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(value = OrderEndpoint.ORDER_URL)
public class OrderEndpoint implements com.zufar.icedlatte.openapi.order.api.OrdersApi {

    public static final String ORDER_URL = "/api/v1/orders";

    private final OrderProvider orderProvider;
    private final OrderCreator orderCreator;

    @Override
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(final CreateNewOrderRequestDto orderRequest) {
        log.info("Received order creation request.");
        var newOrder = orderCreator.createOrder(orderRequest);
        log.info("Order creation request processed.");
        return ResponseEntity.ok().body(newOrder);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getOrders(final List<OrderStatus> orderStatusList) {
        var status = orderStatusList == null ? "Not provided" : orderStatusList.stream().map(OrderStatus::toString).collect(Collectors.joining(", "));
        log.info("Received request to get all orders with status = '{}'", status);
        var orderList = orderProvider.getOrdersByStatus(orderStatusList);
        log.info("Orders retrieval processed");
        return ResponseEntity.ok()
                .body(orderList);
    }
}
