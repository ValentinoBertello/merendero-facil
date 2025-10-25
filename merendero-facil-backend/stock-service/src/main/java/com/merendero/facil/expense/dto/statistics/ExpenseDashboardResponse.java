package com.merendero.facil.expense.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta principal del dashboard con todos los datos financieros y gráficos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseDashboardResponse {
    private ExpenseSummaryData expenseSummaryData;
    private ChartsData chartsData;
}
