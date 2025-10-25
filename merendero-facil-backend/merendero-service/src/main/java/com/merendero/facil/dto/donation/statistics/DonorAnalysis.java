package com.merendero.facil.dto.donation.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Análisis completo de donantes de un periodo (top donantes y distribución por tipo donante)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonorAnalysis {
    private List<TopDonor> topDonors;
    private DonorTypeAnalysis donorTypeAnalysis;
}
