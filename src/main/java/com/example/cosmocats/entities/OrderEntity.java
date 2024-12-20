package com.example.cosmocats.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.NaturalId;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"order\"")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_id_seq")
    @SequenceGenerator(name = "order_id_seq", sequenceName = "order_id_seq")
    private Long id;

    @NaturalId
    @Column(nullable = false)
    private String bankCardId;

    @Column(nullable = false)
    private Float price;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderEntryEntity> entries;
}
