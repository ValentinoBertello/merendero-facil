package com.merendero.facil.expense.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Contiene todos los datos necesarios para renderizar los gráficos del dashboard
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartsData {
    private List<ExpenseByTypeDto> expensesByType;
    private List<ExpenseBySupplyDto> expensesBySupply;
    private List<TimeGroupedExpenseData> timeGroupedData;
}
