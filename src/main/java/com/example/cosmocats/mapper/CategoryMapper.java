package com.example.cosmocats.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.cosmocats.domain.Category;
import com.example.cosmocats.dto.CategoryDto;
import com.example.cosmocats.entities.CategoryEntity;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto categoryToDto(Category category);
    Category dtoToCategory(CategoryDto dto);

    @Mapping(target = "id", expression = "java(uuidToLong(dto.getId()))")
    CategoryEntity dtoToEntity(CategoryDto dto);

    @Mapping(target = "id", expression = "java(longToUuid(entity.getId()))")
    CategoryDto entityToDto(CategoryEntity entity);

    default Long uuidToLong(UUID uuid) {
        return uuid != null ? uuid.getMostSignificantBits() : null;
    }

    default UUID longToUuid(Long id) {
        return id != null ? new UUID(id, 0L) : null;
    }
}
