package com.zufar.icedlatte.order.converter;

import com.zufar.icedlatte.openapi.dto.ShoppingCartItemDto;
import com.zufar.icedlatte.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderItemDtoConverter {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productId", source = "productInfo.id")
    @Mapping(target = "productName", source = "productInfo.name")
    @Mapping(target = "price", source = "productInfo.price")
    @Named("toOrderItemDto")
    OrderItem toOrderItem(ShoppingCartItemDto cartItem);
}
