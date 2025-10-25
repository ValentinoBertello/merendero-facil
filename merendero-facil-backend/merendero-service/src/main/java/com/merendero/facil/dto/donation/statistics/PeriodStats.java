package com.merendero.facil.dto.donation.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Estadísticas de donaciones para un período específico
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeriodStats {
    private BigDecimal totalAmountDonated;
    private Integer donationCount;

    private BigDecimal averagePerDonation;
    private BigDecimal averagePerDay;

    private LocalDate startDate;
    private LocalDate endDate;
}
