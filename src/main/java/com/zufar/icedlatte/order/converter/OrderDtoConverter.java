package com.zufar.icedlatte.order.converter;

import com.zufar.icedlatte.openapi.dto.OrderResponseDto;
import com.zufar.icedlatte.order.entity.Order;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.FIELD)
public interface OrderDtoConverter {

    OrderResponseDto toResponseDto(final Order order);
}
