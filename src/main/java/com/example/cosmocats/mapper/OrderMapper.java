package com.example.cosmocats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.cosmocats.domain.order.Order;
import com.example.cosmocats.entities.OrderEntity;


@Mapper
public interface OrderMapper {
    static OrderMapper getInstance() {
        return Mappers.getMapper(OrderMapper.class);
    }

    @Mapping(target = "id", expression = "java(orderEntity.getId() != null ? UUID.fromString(orderEntity.getId().toString()) : null)")
    @Mapping(target = "entries", expression = "java(orderEntity.getEntries().stream().map(entry -> UUID.fromString(entry.getProduct().getId().toString())).collect(Collectors.toList()))")
    Order entityToDomain(OrderEntity orderEntity);

    @Mapping(target = "id", expression = "java(order.getId() != null ? order.getId().toString() : null)")
    @Mapping(target = "entries", expression = "java(order.getEntryIds().stream().map(id -> { "
            + "OrderEntryEntity entry = new OrderEntryEntity(); "
            + "entry.setProduct(new com.example.cosmocats.entity.ProductEntity(id.toString())); "
            + "return entry; "
            + "}).collect(Collectors.toList()))")
    OrderEntity domainToEntity(Order order);
}