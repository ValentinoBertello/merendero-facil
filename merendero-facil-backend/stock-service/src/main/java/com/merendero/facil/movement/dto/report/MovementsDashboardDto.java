package com.merendero.facil.movement.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO principal para el dashboard de movimientos.
 * Contiene métricas agregadas y estadísticas de ingresos y egresos
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovementsDashboardDto {
    // Promedios
    private BigDecimal avgEntryDay;
    private BigDecimal avgEntryWeek;
    private BigDecimal avgOutputDay;
    private BigDecimal avgOutputWeek;

    // Totales
    private BigDecimal totalEntry;
    private BigDecimal totalOutput;

    // Tipos de entrada
    private BigDecimal entryDonationQty;
    private BigDecimal entryPurchaseQty;

    // Porcentajes
    private BigDecimal percentageVariationEntry;
    private BigDecimal percentageVariationOutput;

    // Grupos de movimientos (la lista puede agrupar meses, semanas o dias)
    List<GroupMovementsDto> groupsMovements;

    public static MovementsDashboardDto empty() {
        BigDecimal zero = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return MovementsDashboardDto.builder()
                .avgEntryDay(zero)
                .avgEntryWeek(zero)
                .avgOutputDay(zero)
                .avgOutputWeek(zero)
                .totalEntry(zero)
                .totalOutput(zero)
                .entryDonationQty(zero)
                .entryPurchaseQty(zero)
                .percentageVariationEntry(zero)
                .percentageVariationOutput(zero)
                .groupsMovements(new ArrayList<>())
                .build();
    }
}