package com.merendero.facil.dto.donation.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Información de un donante destacado en el top de un periodo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopDonor {
    private String donorEmail;
    private BigDecimal amountDonated;
}
