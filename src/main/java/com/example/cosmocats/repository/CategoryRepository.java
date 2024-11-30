package com.example.cosmocats.repository;

import org.springframework.stereotype.Repository;

import com.example.cosmocats.entities.CategoryEntity;

@Repository
public interface CategoryRepository extends NaturalIdRepository<CategoryEntity, Long> {
}

