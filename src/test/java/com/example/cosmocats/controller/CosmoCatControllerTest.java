package com.example.cosmocats.controller;

import com.example.cosmocats.domain.CatInfo;
import com.example.cosmocats.service.CosmoCatService;
import com.example.cosmocats.web.CosmoCatController;
import com.example.featuretoggle.FeatureToggles;
import com.example.featuretoggle.aspect.FeatureToggleAspect;
import com.example.featuretoggle.service.FeatureToggleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CosmoCatController.class)
@Import(FeatureToggleAspect.class)
class CosmoCatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CosmoCatService cosmoCatService;

    @MockBean
    private FeatureToggleService featureToggleService;

    @BeforeEach
    void setup() {
        when(featureToggleService.isFeatureEnabled(FeatureToggles.COSMO_CATS.getFeatureName()))
                .thenReturn(true);
    }

    @Test
    void shouldReturnListOfCosmoCatsWhenFeatureIsEnabled() throws Exception {

        var catInfos = List.of(
                CatInfo.builder()
                        .id(UUID.randomUUID())
                        .name("Cosmo Cat")
                        .build(),
                CatInfo.builder()
                        .id(UUID.randomUUID())
                        .name("Astro Cat")
                        .build(),
                CatInfo.builder()
                        .id(UUID.randomUUID())
                        .name("Star Cat")
                        .build()
        );

        when(cosmoCatService.getAllCatsInfos()).thenReturn(catInfos);

        // When & Then
        mockMvc.perform(get("/api/v1/cosmo-cats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("Cosmo Cat"))
                .andExpect(jsonPath("$[1].name").value("Astro Cat"))
                .andExpect(jsonPath("$[2].name").value("Star Cat"));
    }

    @Test
    void shouldReturn404WhenFeatureIsDisabled() throws Exception {
        when(featureToggleService.isFeatureEnabled(FeatureToggles.COSMO_CATS.getFeatureName()))
                .thenReturn(false);

        mockMvc.perform(get("/api/v1/cosmo-cats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
