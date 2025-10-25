package com.merendero.facil.expense.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Representa el total de gastos agrupados por nombre de insumo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseBySupplyDto {
    private String supplyName;
    private BigDecimal amount;
}
