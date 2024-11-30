package com.example.cosmocats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cosmocats.entities.OrderEntryEntity;

@Repository
public interface OrderEntryRepository extends JpaRepository<OrderEntryEntity, Long> {
}
