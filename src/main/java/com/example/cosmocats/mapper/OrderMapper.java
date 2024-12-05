package com.example.cosmocats.mapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.cosmocats.domain.order.Order;
import com.example.cosmocats.domain.order.OrderEntry;
import com.example.cosmocats.dto.order.OrderDto;
import com.example.cosmocats.entities.OrderEntity;
import com.example.cosmocats.entities.OrderEntryEntity;
import com.example.cosmocats.entities.ProductEntity;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    
    @Mapping(target = "entryIds", source = "entries")
    OrderDto orderToDto(Order order);

    @Mapping(target = "entries", source = "entryIds")
    Order dtoToOrder(OrderDto dto);

    default List<UUID> entriesToEntryIds(List<OrderEntry> entries) {
        return entries.stream().map(entry -> entry.getProduct().getId()).collect(Collectors.toList());
    }

    default List<OrderEntry> entryIdsToEntries(List<UUID> entryIds) {
        return entryIds.stream()
                .map(id -> OrderEntry.builder()
                        .product(com.example.cosmocats.domain.Product.builder()
                                .id(id)
                                .build())
                        .build())
                .collect(Collectors.toList());
    }

    @Mapping(target = "id", expression = "java(uuidToLong(dto.getId()))")
    @Mapping(target = "entries", source = "entryIds")
    OrderEntity dtoToEntity(OrderDto dto);

    @Mapping(target = "id", expression = "java(longToUuid(entity.getId()))")
    @Mapping(target = "entryIds", source = "entries")
    OrderDto entityToDto(OrderEntity entity);

    default Long uuidToLong(UUID uuid) {
        return uuid != null ? uuid.getMostSignificantBits() : null;
    }

    default UUID longToUuid(Long id) {
        return id != null ? new UUID(id, 0L) : null;
    }

    default List<UUID> entitiesToEntryIds(List<OrderEntryEntity> entries) {
        return entries.stream()
            .map(entry -> entry.getProduct() != null ? longToUuid(entry.getProduct().getId()) : null)
            .collect(Collectors.toList());
    }

    default List<OrderEntryEntity> entryIdsToEntities(List<UUID> entryIds) {
        return entryIds.stream()
            .map(id -> OrderEntryEntity.builder()
                .product(ProductEntity.builder()
                    .id(uuidToLong(id))
                    .build())
                .build())
            .collect(Collectors.toList());
    }
}