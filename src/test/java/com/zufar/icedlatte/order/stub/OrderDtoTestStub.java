package com.zufar.icedlatte.order.stub;

import com.zufar.icedlatte.openapi.dto.OrderRequestDto;

public class OrderDtoTestStub {

    public static OrderRequestDto createOrderRequestDto() {
        OrderRequestDto orderDto = new OrderRequestDto();
        orderDto.setCity("London");
        orderDto.setCountry("UK");
        orderDto.setRecipientName("Jane");
        orderDto.setRecipientSurname("Doe");
        orderDto.setPostCode("35879425");
        return orderDto;
    }
}
