package com.zufar.icedlatte.order.converter;

import com.zufar.icedlatte.openapi.dto.OrderResponseDto;
import com.zufar.icedlatte.openapi.dto.OrderStatus;
import com.zufar.icedlatte.openapi.dto.ShoppingCartDto;
import com.zufar.icedlatte.order.entity.Order;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.FIELD,
        uses = {OrderStatus.class})
public interface OrderDtoConverter {

    OrderResponseDto toResponseDto(final Order orderEntity);

    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "deliveryAddress", source = "addressId")
    @Mapping(target = "items", ignore = true)
    // @Mapping(target = "items", source = "cartDto.items", qualifiedByName = {"toOrderItemDto"})
    Order toOrder(final ShoppingCartDto cartDto, final UUID addressId);
}
