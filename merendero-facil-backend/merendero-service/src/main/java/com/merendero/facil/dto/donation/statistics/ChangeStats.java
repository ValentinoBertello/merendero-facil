package com.merendero.facil.dto.donation.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Representa el cambio absoluto y porcentual que sufrió un valor como:
 * montos donados, cantidad de donaciones, etc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeStats {
    private BigDecimal value; //Cambio absoluto
    private BigDecimal percentage; //cambio porcentual
}
