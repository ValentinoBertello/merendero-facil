package com.merendero.facil.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Dto que representa un lote de insumo.
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LotDto {
    private Long id;
    private BigDecimal initialQuantity;
    private BigDecimal currentQuantity;
    private LocalDate expirationDate;
    private int daysToExpire; // Días restantes hasta vencimiento
}
