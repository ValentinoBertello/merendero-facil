package com.merendero.facil.movement.entity;

import com.merendero.facil.movement.dto.entry.EntryType;
import com.merendero.facil.supply.entity.SupplyEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa una entrada de insumos en el inventario, con su cantidad, fecha y tipo
 **/
@Entity
@Table(name = "entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supply_id")
    private SupplyEntity supply;

    private BigDecimal quantity;

    @Column(name = "entry_date")
    private LocalDateTime entryDate;

    @Enumerated(EnumType.STRING)  // Mapea el enum como String
    @Column(name = "entry_type")
    private EntryType entryType;

    @PrePersist
    public void beforePersist() {
        entryDate = LocalDateTime.now();
    }
}
