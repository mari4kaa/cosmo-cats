package com.example.config;

import org.springframework.beans.factory.annotation.Value;

import lombok.Getter;

public class FeatureToggleService {
    @Value("${feature.cosmoCats.enabled}")
    @Getter
    private boolean cosmoCats;

    @Value("${feature.kittyProducts.enabled}")
    @Getter
    private boolean kittyProducts;
}
