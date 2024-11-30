package com.example.cosmocats.mapper;

import java.util.List;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.example.cosmocats.domain.Product;
import com.example.cosmocats.dto.ProductDto;
import com.example.cosmocats.entities.ProductEntity;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    static ProductMapper getInstance() {
        return Mappers.getMapper(ProductMapper.class);
    }

    @Mapping(target = "categoryId", source = "category.id")
    ProductDto productToDto(Product product);

    @Mapping(target = "category.id", source = "categoryId")
    Product dtoToProduct(ProductDto productDto);

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "categoryId", source = "category.id")
    ProductDto entityToDto(ProductEntity productEntity);

    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "id", ignore = true)
    ProductEntity dtoToEntity(ProductDto productDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category.id", source = "categoryId")
    void updateEntityFromDto(ProductDto productDto, @MappingTarget ProductEntity productEntity);

    List<ProductDto> EntitiesToDtos(List<ProductEntity> products);
    List<ProductEntity> dtosToEntities(List<ProductDto> productDtos);

    default Long mapCategoryId(UUID categoryId) {
        return categoryId != null ? categoryId.getMostSignificantBits() : null;
    }

    default UUID mapCategoryUUID(Long categoryId) {
        return categoryId != null ? new UUID(categoryId, 0) : null;
    }
}