package com.example.cosmocats.web;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cosmocats.domain.CatInfo;
import com.example.cosmocats.dto.CatInfoDto;
import com.example.cosmocats.mapper.CatInfoMapper;
import com.example.cosmocats.service.CosmoCatService;
import com.example.featuretoggle.FeatureToggles;
import com.example.featuretoggle.annotation.FeatureToggle;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/v1/cosmo-cats")
public class CosmoCatController {
    private final CosmoCatService cosmoCatService;
    private final CatInfoMapper catInfoMapper = CatInfoMapper.getInstance();

    public CosmoCatController(CosmoCatService cosmoCatService) {
        this.cosmoCatService = cosmoCatService;
    }

    @GetMapping
    @FeatureToggle(FeatureToggles.COSMO_CATS)
    public ResponseEntity<List<CatInfoDto>> getCosmoCats() {
        List<CatInfo> catInfos = cosmoCatService.getAllCatsInfos();
        List<CatInfoDto> catInfoDtos = catInfos.stream()
                .map(catInfoMapper::catInfoToDto)
                .toList();
        return ResponseEntity.ok(catInfoDtos);
    }
}
