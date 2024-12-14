package com.example.cosmocats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.cosmocats.entities.OrderEntryEntity;
import com.example.cosmocats.projection.ProductReport;

import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OrderEntryRepository extends JpaRepository<OrderEntryEntity, Long> {

    @Query("SELECT p.name AS productName, SUM(oe.quantity) AS totalQuantity " +
            "FROM OrderEntryEntity oe " +
            "JOIN oe.product p " +
            "GROUP BY p.name " +
            "ORDER BY totalQuantity DESC")
    List<ProductReport> findMostFrequentlyBoughtProducts();
}