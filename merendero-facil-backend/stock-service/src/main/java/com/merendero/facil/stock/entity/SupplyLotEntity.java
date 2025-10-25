package com.merendero.facil.stock.entity;

import com.merendero.facil.movement.entity.EntryEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que representa un lote de insumos, con su cantidad y fecha de vencimiento y estado de notificación.
 **/
@Entity
@Table(name = "lots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyLotEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "entrada_id")
    private EntryEntity entry;

    @Column(name = "initial_quantity")
    private BigDecimal initialQuantity;

    @Column(name = "current_quantity")
    private BigDecimal currentQuantity;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    private boolean notified;
}