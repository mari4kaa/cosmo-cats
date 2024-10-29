package com.example.cosmocats.mapper;

import java.util.List;

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

    default long[] entriesToEntryIds(List<OrderEntry> entries) {
        return entries.stream().mapToLong(entry -> entry.getProduct().getId()).toArray();
    }
}
