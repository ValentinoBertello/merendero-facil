package com.merendero.facil.expense.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa una categoría de gastos en la base de datos (Compra insumos/ Luz y Gas/ Otros)
 **/
@Entity
@Table(name = "types_expense")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
}
