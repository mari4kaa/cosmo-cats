package com.example.cosmocats.mapper;

import java.util.List;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.example.cosmocats.domain.Product;
import com.example.cosmocats.dto.ProductDto;
import com.example.cosmocats.entities.ProductEntity;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    @Mapping(target = "categoryId", source = "category.id")
    ProductDto productToDto(Product product);

    @Mapping(target = "category.id", source = "categoryId")
    Product dtoToProduct(ProductDto productDto);

    @Mapping(target = "id", expression = "java(uuidToLong(productDto.getId()))")
    @Mapping(target = "category.id", source = "categoryId", qualifiedByName = "mapCategoryId")
    ProductEntity dtoToEntity(ProductDto productDto);

    @Mapping(target = "id", expression = "java(longToUuid(productEntity.getId()))")
    @Mapping(target = "categoryId", source = "category.id", qualifiedByName = "mapCategoryUUID")
    ProductDto entityToDto(ProductEntity productEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category.id", source = "categoryId", qualifiedByName = "mapCategoryId")
    void updateEntityFromDto(ProductDto productDto, @MappingTarget ProductEntity productEntity);

    @Named("mapCategoryId")
    default Long mapCategoryId(UUID categoryId) {
        return categoryId != null ? categoryId.getMostSignificantBits() : null;
    }

    @Named("mapCategoryUUID")
    default UUID mapCategoryUUID(Long categoryId) {
        return categoryId != null ? new UUID(categoryId, 0L) : null;
    }

    default Long uuidToLong(UUID uuid) {
        return uuid != null ? uuid.getMostSignificantBits() : null;
    }

    default UUID longToUuid(Long id) {
        return id != null ? new UUID(id, 0L) : null;
    }

    List<ProductDto> entitiesToDtos(List<ProductEntity> products);
    List<ProductEntity> dtosToEntities(List<ProductDto> productDtos);
    
}