package com.example.cosmocats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.cosmocats.domain.Product;
import com.example.cosmocats.dto.ProductDto;

@Mapper(uses = CategoryMapper.class)
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "categoryId", source = "category.id")
    ProductDto productToDto(Product product);

    @Mapping(target = "category.id", source = "categoryId")
    Product dtoToProduct(ProductDto productDto);
}