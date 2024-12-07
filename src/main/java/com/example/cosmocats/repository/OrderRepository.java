package com.example.cosmocats.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.cosmocats.entities.OrderEntity;

@Repository
public interface OrderRepository extends NaturalIdRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByBankCardId(String bankCardId);
}
