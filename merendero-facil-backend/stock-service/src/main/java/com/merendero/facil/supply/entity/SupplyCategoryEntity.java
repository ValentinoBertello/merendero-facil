package com.merendero.facil.supply.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Entidad que representa una categoría de insumos en la base de datos (Alimento/ Bebida/ Otro) **/
@Entity
@Table(name = "supply_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;
}
