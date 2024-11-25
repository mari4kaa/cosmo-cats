package com.example.cosmocats.service;

import com.example.featuretoggle.FeatureToggles;
import com.example.featuretoggle.annotation.FeatureToggle;
import com.example.featuretoggle.exceptions.FeatureNotAvailableException;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CosmoCatService {
    
    @FeatureToggle(feature = FeatureToggles.COSMO_CATS)
    public List<String> getCosmoCats() throws FeatureNotAvailableException {
        return List.of("Space Cat", "Galaxy Cat", "Star Cat");
    }
}