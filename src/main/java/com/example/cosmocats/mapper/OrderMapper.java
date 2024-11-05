package com.example.cosmocats.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.cosmocats.domain.order.Order;
import com.example.cosmocats.domain.order.OrderEntry;
import com.example.cosmocats.dto.order.OrderDto;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "entryIds", source = "entries")
    OrderDto orderToDto(Order order);

    @Mapping(target = "entries", source = "entryIds")
    Order dtoToOrder(OrderDto dto);

    default List<Long> entriesToEntryIds(List<OrderEntry> entries) {
        return entries.stream().map((entry) -> entry.getProduct().getId()).collect(Collectors.toList());
    }

    default List<OrderEntry> entryIdsToEntries(List<Long> entryIds) {
        return entryIds.stream()
                .map(id -> OrderEntry.builder()
                        .product(com.example.cosmocats.domain.Product.builder()
                                .id(id)
                                .build())
                        .build())
                .collect(Collectors.toList());
    }
}