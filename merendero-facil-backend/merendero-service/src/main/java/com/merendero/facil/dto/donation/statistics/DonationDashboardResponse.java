package com.merendero.facil.dto.donation.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Respuesta principal del dashboard de donaciones con todos los datos, comparaciones y
 * estadísticas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationDashboardResponse {
    private PeriodStats currentPeriod;
    private PeriodStats previousPeriod;
    private ComparisonStats comparisonStats;
    private List<DonationDateSummary> donationDateSummaries = new ArrayList<>();
    private DonorAnalysis donorAnalysis;
}
