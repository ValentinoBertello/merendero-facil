package com.merendero.facil.expense.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Métricas resumidas del dashboard (totales, promedios y porcentajes)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseSummaryData {
    private BigDecimal totalExpenseAmount;
    private BigDecimal totalDonationAmount;
    private BigDecimal totalProfitOrLoss;
    private BigDecimal dailyProfitOrLoss;
    private BigDecimal profitOrLossPercentage;
}
