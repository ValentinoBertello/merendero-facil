package com.merendero.facil.dto.donation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa la información que se envía desde el servidor al cliente
 * después de crear, actualizar o consultar uma Donación.
 * **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationResponseDto {
    private Long id;
    private Long userId;
    private String userEmail;
    private Long merenderoId;
    private LocalDateTime donationDate;
    private String paymentId;

    private BigDecimal grossAmount;
    private BigDecimal mpFee;
    private BigDecimal netAmount;
}
