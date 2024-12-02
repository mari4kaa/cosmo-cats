package com.example.cosmocats.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.cosmocats.domain.Product;
import com.example.cosmocats.domain.order.OrderEntry;
import com.example.cosmocats.entities.OrderEntryEntity;
import com.example.cosmocats.entities.ProductEntity;

@Mapper
public interface OrderEntryMapper {
    static OrderEntryMapper getInstance() {
        return Mappers.getMapper(OrderEntryMapper.class);
    }

    @Mapping(target = "product", source = "product")
    OrderEntry entityToDomain(OrderEntryEntity entity);

    @Mapping(target = "product", source = "product")
    OrderEntryEntity domainToEntity(OrderEntry domain);

    default ProductEntity mapProductToEntity(Product product) {
        if (product == null) return null;
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(uuidToLong(product.getId()));
        return productEntity;
    }

    default Product mapProductToDomain(ProductEntity productEntity) {
        if (productEntity == null) return null;
        return Product.builder()
            .id(longToUuid(productEntity.getId()))
            .build();
    }

    default Long uuidToLong(UUID uuid) {
        return uuid != null ? uuid.getMostSignificantBits() : null;
    }

    default UUID longToUuid(Long id) {
        return id != null ? new UUID(id, 0L) : null;
    }
}
