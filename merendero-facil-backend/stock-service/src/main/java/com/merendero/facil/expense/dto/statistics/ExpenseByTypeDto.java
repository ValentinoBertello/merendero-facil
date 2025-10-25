package com.merendero.facil.expense.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Representa el total de gastos agrupados por tipo de gasto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseByTypeDto {
    private String expenseType;
    private BigDecimal amount;
}
