package com.example.cosmocats.repository;

import org.springframework.stereotype.Repository;

import com.example.cosmocats.entities.OrderEntity;

@Repository
public interface OrderRepository extends NaturalIdRepository<OrderEntity, Long> {
}
