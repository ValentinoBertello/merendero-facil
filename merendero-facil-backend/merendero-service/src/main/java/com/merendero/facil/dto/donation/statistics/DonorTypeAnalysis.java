package com.merendero.facil.dto.donation.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Distribución de donantes por tipo (nuevos vs recurrentes)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonorTypeAnalysis {
    private Integer newDonorsCount;
    private Integer recurrentDonorsCount;
}
