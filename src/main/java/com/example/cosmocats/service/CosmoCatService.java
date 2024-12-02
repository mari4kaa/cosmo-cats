package com.example.cosmocats.service;

import com.example.cosmocats.domain.CatInfo;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class CosmoCatService {

    public List<CatInfo> getAllCatsInfos() {
        return List.of(
                CatInfo.builder().id(UUID.randomUUID()).name("Cosmo Cat").build(),
                CatInfo.builder().id(UUID.randomUUID()).name("Astro Cat").build(),
                CatInfo.builder().id(UUID.randomUUID()).name("Star Cat").build());
    }
}
