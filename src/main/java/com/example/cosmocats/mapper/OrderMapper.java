package com.example.cosmocats.mapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.cosmocats.domain.order.Order;
import com.example.cosmocats.domain.order.OrderEntry;
import com.example.cosmocats.dto.order.OrderDto;
import com.example.cosmocats.dto.order.OrderEntryDto;
import com.example.cosmocats.entities.OrderEntity;
import com.example.cosmocats.entities.OrderEntryEntity;
import com.example.cosmocats.entities.ProductEntity;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "entryDtos", source = "entries")
    OrderDto orderToDto(Order order);

    @Mapping(target = "entries", source = "entryDtos")
    Order dtoToOrder(OrderDto dto);

    default List<OrderEntryDto> entriesToEntryDtos(List<OrderEntry> entries) {
        return entries.stream()
            .map(entry -> OrderEntryDto.builder()
                .productId(entry.getProduct().getId())
                .quantity(entry.getQuantity())
                .build())
            .collect(Collectors.toList());
    }

    default List<OrderEntry> entryDtosToEntries(List<OrderEntryDto> entryDtos) {
        return entryDtos.stream()
            .map(dto -> OrderEntry.builder()
                .product(com.example.cosmocats.domain.Product.builder()
                    .id(dto.getProductId())
                    .build())
                .quantity(dto.getQuantity())
                .build())
            .collect(Collectors.toList());
    }

    @Mapping(target = "id", expression = "java(uuidToLong(dto.getId()))")
    @Mapping(target = "entries", source = "entryDtos")
    OrderEntity dtoToEntity(OrderDto dto);

    @Mapping(target = "id", expression = "java(longToUuid(entity.getId()))")
    @Mapping(target = "entryDtos", source = "entries")
    OrderDto entityToDto(OrderEntity entity);

    default Long uuidToLong(UUID uuid) {
        return uuid != null ? uuid.getMostSignificantBits() : null;
    }

    default UUID longToUuid(Long id) {
        return id != null ? new UUID(id, 0L) : null;
    }

    default List<OrderEntryDto> entitiesToEntryDtos(List<OrderEntryEntity> entries) {
        return entries.stream()
            .map(entry -> OrderEntryDto.builder()
                .productId(longToUuid(entry.getProduct().getId()))
                .quantity(entry.getQuantity())
                .build())
            .collect(Collectors.toList());
    }

    default List<OrderEntryEntity> entryDtosToEntities(List<OrderEntryDto> entryDtos) {
        return entryDtos.stream()
            .map(dto -> OrderEntryEntity.builder()
                .product(ProductEntity.builder()
                    .id(uuidToLong(dto.getProductId()))
                    .build())
                .quantity(dto.getQuantity())
                .build())
            .collect(Collectors.toList());
    }
}
