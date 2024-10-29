package com.example.cosmocats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.cosmocats.domain.Category;
import com.example.cosmocats.dto.CategoryDto;

@Mapper
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper( CategoryMapper.class );

    CategoryDto categoryToDto(Category category);
    Category dtoToCategory(CategoryDto dto);
}
