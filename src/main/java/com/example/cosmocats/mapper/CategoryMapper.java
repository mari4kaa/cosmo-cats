package com.example.cosmocats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.cosmocats.domain.Category;
import com.example.cosmocats.dto.CategoryDto;
import com.example.cosmocats.entities.CategoryEntity;

@Mapper
public interface CategoryMapper {
    static CategoryMapper getInstance() {
        return Mappers.getMapper(CategoryMapper.class);
    }

    @Mapping(target = "id", expression = "java(categoryEntity.getId() != null ? UUID.fromString(categoryEntity.getId().toString()) : null)")
    Category entityToDomain(CategoryEntity categoryEntity);

    @Mapping(target = "id", expression = "java(category.getId() != null ? category.getId().toString() : null)")
    CategoryEntity domainToEntity(Category category);

    @Mapping(target = "id", expression = "java(category.getId() != null ? category.getId().toString() : null)")
    CategoryDto entityToDto(CategoryEntity categoryEntity);

    @Mapping(target = "id", expression = "java(categoryDto.getId() != null ? UUID.fromString(categoryDto.getId()) : null)")
    CategoryEntity dtoToEntity(CategoryDto categoryDto);
}
