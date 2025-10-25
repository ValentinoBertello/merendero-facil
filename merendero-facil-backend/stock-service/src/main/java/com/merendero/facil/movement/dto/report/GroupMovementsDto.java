package com.merendero.facil.movement.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para agrupar movimientos por período de tiempo (día, semana o mes)
 * Representa los totales de ingresos y egresos para un período específico
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMovementsDto {
    private LocalDate date;
    private BigDecimal entryDonationQty;
    private BigDecimal entryPurchaseQty;
    private BigDecimal outputQty;
    private String label;
}