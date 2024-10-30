package com.example.cosmocats.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.cosmocats.domain.order.Order;
import com.example.cosmocats.domain.order.OrderEntry;
import com.example.cosmocats.dto.order.OrderDto;
import com.example.cosmocats.dto.order.OrderEntryDto;

@Mapper(uses = {OrderEntryMapper.class})
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "entryIds", source = "entries")
    OrderDto orderToDto(Order order);

    @Mapping(target = "entries", source = "entryIds")
    Order dtoToOrder(OrderDto dto);

    default long[] entriesToEntryIds(List<OrderEntry> entries) {
        return entries.stream()
                .mapToLong(entry -> entry.getProduct().getId())
                .toArray();
    }

    default List<OrderEntry> entryIdsToEntries(long[] entryIds) {
        return java.util.Arrays.stream(entryIds)
                .mapToObj(id -> OrderEntry.builder()
                        .product(com.example.cosmocats.domain.Product.builder()
                                .id(id)
                                .build())
                        .build())
                .toList();
    }
}