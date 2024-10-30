package com.example.cosmocats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.cosmocats.domain.order.OrderEntry;
import com.example.cosmocats.dto.order.OrderEntryDto;

@Mapper(uses = ProductMapper.class)
public interface OrderEntryMapper {
    OrderEntryMapper INSTANCE = Mappers.getMapper(OrderEntryMapper.class);

    @Mapping(target = "productId", source = "product.id")
    OrderEntryDto orderEntryToDto(OrderEntry entry);

    @Mapping(target = "product.id", source = "productId")
    OrderEntry dtoToOrderEntry(OrderEntryDto dto);
}
