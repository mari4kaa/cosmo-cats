package com.example.cosmocats.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.example.cosmocats.repository.impl.NaturalIdRepositoryImpl;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(
    basePackages = "com.example.cosmocats.repository",
    repositoryBaseClass = NaturalIdRepositoryImpl.class)
public class JpaRepositoryConfiguration {
}