package com.zufar.icedlatte.order.converter;

import com.zufar.icedlatte.openapi.dto.OrderResponseDto;
import com.zufar.icedlatte.openapi.dto.OrderStatus;
import com.zufar.icedlatte.openapi.dto.ShoppingCartDto;
import com.zufar.icedlatte.openapi.dto.ShoppingCartItemDto;
import com.zufar.icedlatte.order.entity.Order;
import com.zufar.icedlatte.order.entity.OrderItem;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.FIELD,
        uses = {OrderStatus.class})
public interface OrderDtoConverter {

    OrderResponseDto toResponseDto(final Order orderEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "items", source = "shoppingCartDto.items", qualifiedByName = "toOrderItems")
    @Mapping(target = "itemsQuantity", ignore = true)
    @Mapping(target = "itemsTotalPrice", ignore = true)
    @Mapping(target = "deliveryAddress", source = "deliveryAddress")
    Order toOrder(final ShoppingCartDto shoppingCartDto,
                  final UUID deliveryAddress,
                  final UUID userId);

    @Named("toOrderItems")
    default List<OrderItem> toOrderItems(List<ShoppingCartItemDto> items) {
        return items.stream()
                .map(this::toOrderItem)
                .toList();
    }

    @Named("toOrderItemDto")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productId", source = "productInfo.id")
    @Mapping(target = "productName", source = "productInfo.name")
    @Mapping(target = "productPrice", source = "productInfo.price")
    @Mapping(target = "productsQuantity", source = "productQuantity")
    OrderItem toOrderItem(ShoppingCartItemDto cartItem);
}
