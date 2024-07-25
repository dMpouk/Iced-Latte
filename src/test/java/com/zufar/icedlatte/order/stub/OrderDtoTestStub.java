package com.zufar.icedlatte.order.stub;

import com.zufar.icedlatte.openapi.dto.AddressDto;
import com.zufar.icedlatte.openapi.dto.CreateNewOrderRequestDto;

public class OrderDtoTestStub {

    public static CreateNewOrderRequestDto createOrderRequestDto() {
        CreateNewOrderRequestDto orderDto = new CreateNewOrderRequestDto();
        var address = new AddressDto("UK", "London", "Example Lane", "35879425");
        orderDto.setAddress(address);
        orderDto.setRecipientName("Jane");
        orderDto.setRecipientSurname("Doe");
        return orderDto;
    }
}
