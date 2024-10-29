package com.example.cosmocats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import com.example.cosmocats.domain.order.OrderEntry;
import com.example.cosmocats.dto.order.OrderEntryDto;

@Mapper
public interface OrderEntryMapper {
    OrderEntryMapper INSTANCE = Mappers.getMapper(OrderEntryMapper.class);

    @Mapping(target = "productId", source = "product.id")
    OrderEntryDto orderToDto(OrderEntry entry);
}
