package com.merendero.facil.dto.donation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de solicitud para la inserción de una donación.
 * **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationRequestDto {
    private String userEmail;
    private Long merenderoId;
    private LocalDateTime donationDate;
    private String paymentId;

    private BigDecimal grossAmount;
    private BigDecimal mpFee;
    private BigDecimal netAmount;
}
