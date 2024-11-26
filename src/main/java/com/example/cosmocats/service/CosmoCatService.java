package com.example.cosmocats.service;

import com.example.cosmocats.dto.CatInfoDto;
import com.example.featuretoggle.FeatureToggles;
import com.example.featuretoggle.annotation.FeatureToggle;
import com.example.featuretoggle.exceptions.FeatureNotAvailableException;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class CosmoCatService {

    @FeatureToggle(feature = FeatureToggles.COSMO_CATS)
    public List<CatInfoDto> getCosmoCats() throws FeatureNotAvailableException {
        return List.of(
                CatInfoDto.builder().id(UUID.randomUUID()).name("Cosmo Cat").build(),
                CatInfoDto.builder().id(UUID.randomUUID()).name("Astro Cat").build(),
                CatInfoDto.builder().id(UUID.randomUUID()).name("Star Cat").build());
    }
}
