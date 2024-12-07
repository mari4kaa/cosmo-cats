package com.example.cosmocats.mapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.cosmocats.domain.order.Order;
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

    @Mapping(target = "bankCardId", ignore = true) // BankCardId is not overwritten during update
    void updateEntityFromDto(OrderDto dto, @MappingTarget OrderEntity entity);

    @Mapping(target = "id", expression = "java(uuidToLong(dto.getId()))")
    @Mapping(target = "entries", source = "entryDtos")
    default OrderEntity dtoToEntity(OrderDto dto) {
        OrderEntity orderEntity = new OrderEntity();
        
        orderEntity.setBankCardId(dto.getBankCardId());
        
        orderEntity.setPrice(dto.getPrice());

        // Link entries to order
        List<OrderEntryEntity> entryEntities = entryDtosToEntities(dto.getEntryDtos());
        entryEntities.forEach(entry -> entry.setOrder(orderEntity));
        orderEntity.setEntries(entryEntities);

        return orderEntity;
    }

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
