package com.example.cosmocats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.cosmocats.domain.Product;
import com.example.cosmocats.dto.ProductDto;
import com.example.cosmocats.entities.ProductEntity;


@Mapper
public interface ProductMapper {
    static ProductMapper getInstance() {
        return Mappers.getMapper(ProductMapper.class);
    }

    @Mapping(target = "id", expression = "java(productEntity.getId() != null ? UUID.fromString(productEntity.getId().toString()) : null)")
    @Mapping(target = "category", expression = "java(productEntity.getCategory() != null ? UUID.fromString(productEntity.getCategory().getId().toString()) : null)")
    Product entityToDomain(ProductEntity productEntity);

    @Mapping(target = "id", expression = "java(product.getId() != null ? product.getId().toString() : null)")
    @Mapping(target = "category.id", expression = "java(product.getCategoryId() != null ? product.getCategoryId().toString() : null)")
    ProductEntity domainToEntity(Product product);

    @Mapping(target = "id", expression = "java(productEntity.getId() != null ? UUID.fromString(productEntity.getId().toString()) : null)")
    @Mapping(target = "categoryId", expression = "java(productEntity.getCategory() != null ? UUID.fromString(productEntity.getCategory().getId().toString()) : null)")
    ProductDto entityToDto(ProductEntity productEntity);

    @Mapping(target = "id", expression = "java(product.getId() != null ? product.getId().toString() : null)")
    @Mapping(target = "category.id", expression = "java(product.getCategoryId() != null ? product.getCategoryId().toString() : null)")
    ProductEntity dtoToEntity(ProductDto productDto);

    @Mapping(target = "categoryId", source = "category.id")
    ProductDto productToDto(Product product);

    @Mapping(target = "category.id", source = "categoryId")
    Product dtoToProduct(ProductDto productDto);
}
