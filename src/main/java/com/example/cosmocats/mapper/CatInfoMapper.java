package com.example.cosmocats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.cosmocats.domain.CatInfo;
import com.example.cosmocats.dto.CatInfoDto;

@Mapper
public interface CatInfoMapper {
    static CatInfoMapper getInstance() {
        return Mappers.getMapper(CatInfoMapper.class);
    }

    CatInfoDto catInfoToDto(CatInfo catInfo);
    CatInfo dtoToCategory(CatInfoDto dto);
}
