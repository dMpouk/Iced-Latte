package com.zufar.icedlatte.payment.converter;

import com.zufar.icedlatte.cart.converter.ShoppingCartItemDtoConverter;
import com.zufar.icedlatte.openapi.dto.ProcessedPaymentDetailsDto;
import com.zufar.icedlatte.order.entity.OrderItem;
import com.zufar.icedlatte.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.List;

@Mapper(uses = ShoppingCartItemDtoConverter.class,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = BigDecimal.class)
public interface PaymentConverter {

    @Mapping(target = "taxRate", expression = "java(BigDecimal.valueOf(0.05))")
    @Mapping(target = "shippingCost", expression = "java(BigDecimal.valueOf(5.00))")
    ProcessedPaymentDetailsDto toDto(final Payment payment,
                                     final List<OrderItem> orderItems);
}
