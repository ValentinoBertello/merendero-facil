package com.merendero.facil.dto.donation.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa el aumento o disminución de los montos donados de un periodo
 * con respecto de otro (actual vs anterior).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComparisonStats {
    private ChangeStats amountDonatedChange;
    private ChangeStats donationCountChange;
}
