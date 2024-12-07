package com.example.cosmocats.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.example.cosmocats.entities.OrderEntity;

@Repository
public interface OrderRepository extends NaturalIdRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByNaturalId(UUID naturalId);
}
