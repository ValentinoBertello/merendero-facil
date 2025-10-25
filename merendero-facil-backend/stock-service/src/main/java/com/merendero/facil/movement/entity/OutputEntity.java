package com.merendero.facil.movement.entity;

import com.merendero.facil.supply.entity.SupplyEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa una salida de insumos en el inventario, con su cantidad y fecha.
 **/
@Entity
@Table(name = "outputs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutputEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supply_id")
    private SupplyEntity supply;

    private BigDecimal quantity;

    @Column(name = "output_date")
    private LocalDateTime outputDate;

    @PrePersist
    public void beforePersist() {
        outputDate = LocalDateTime.now();
    }
}
