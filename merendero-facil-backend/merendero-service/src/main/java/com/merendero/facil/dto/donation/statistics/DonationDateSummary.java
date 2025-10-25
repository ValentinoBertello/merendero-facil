package com.merendero.facil.dto.donation.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Resumen de donaciones agrupadas por fecha (día, semana o mes)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationDateSummary {
    private LocalDate date;
    private BigDecimal amountDonated;
    private String label;
}
