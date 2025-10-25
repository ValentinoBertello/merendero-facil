package com.merendero.facil.expense.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos de gastos y donaciones agrupados por período de tiempo (día/semana/mes)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeGroupedExpenseData {
    private LocalDate date;
    private BigDecimal expenseAmount;
    private BigDecimal donationAmount;
    private String label;
}
