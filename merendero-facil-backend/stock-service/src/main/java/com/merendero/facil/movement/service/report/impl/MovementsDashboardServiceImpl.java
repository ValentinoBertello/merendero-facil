package com.merendero.facil.movement.service.report.impl;

import com.merendero.facil.movement.dto.entry.EntryType;
import com.merendero.facil.movement.dto.report.GroupMovementsDto;
import com.merendero.facil.movement.dto.report.MovementsDashboardDto;
import com.merendero.facil.movement.entity.EntryEntity;
import com.merendero.facil.movement.entity.OutputEntity;
import com.merendero.facil.movement.repository.EntryRepository;
import com.merendero.facil.movement.repository.OutputRepository;
import com.merendero.facil.movement.service.report.GroupMovementsService;
import com.merendero.facil.movement.service.report.MovementsDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovementsDashboardServiceImpl implements MovementsDashboardService {

    private final GroupMovementsService groupMovementsService;
    private final EntryRepository entryRepository;
    private final OutputRepository outputRepository;

    /**
     * Obtiene el resumen de movimientos (entradas y salidas) para un insumo.
     * Con lista de `GroupMovementsDto`, promedios, totales, etc.
     **/
    @Override
    @Transactional(readOnly = true)
    public MovementsDashboardDto getSummaryMovements(Long supplyId,
                                                     LocalDate startDate,
                                                     LocalDate endDate,
                                                     String groupBy) {
        // Inicializa el DTO con ceros y lista vacía
        MovementsDashboardDto summaryMovements = MovementsDashboardDto.empty();

        // Obtiene todas las entradas con los filtros pedidos
        List<EntryEntity> entries =
                entryRepository.findByDatesAndSupply(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), supplyId);
        // Obtiene todas las salidas con los filtros pedidos
        List<OutputEntity> outputs =
                outputRepository.findByDatesAndSupply(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), supplyId);

        if (this.entriesAndOutputsEmpties(entries, outputs)){
            return summaryMovements;
        }

        this.applyGroups(entries, outputs, groupBy, summaryMovements);
        this.applyData(entries, outputs, summaryMovements, startDate, endDate);
        return summaryMovements;
    }

    /**
     * Si no hay datos en ese rango/insumo se devuelve el summary vacío (ya inicializado)
     **/
    private boolean entriesAndOutputsEmpties(List<EntryEntity> entries, List<OutputEntity> outputs) {
        return (entries == null || entries.isEmpty()) && (outputs == null || outputs.isEmpty());
    }

    private void applyData(List<EntryEntity> entries, List<OutputEntity> outputs, MovementsDashboardDto summaryMovements,
                           LocalDate startDate, LocalDate endDate) {
        // Aplica valores totales
        applyTotalQuantities(entries, outputs, summaryMovements);

        // Aplica los promedios
        applyDailyAverages(summaryMovements, startDate, endDate);
        applyWeeklyAverages(summaryMovements, startDate, endDate);

        // Aplica porcentajes
        applyPercentages(summaryMovements, summaryMovements.getTotalEntry(), summaryMovements.getTotalOutput());
    }

    /**
     *  Obtiene una lista de entradas y salidas agrupadas por dias, meses o semanas
     **/
    private void applyGroups(List<EntryEntity> entries, List<OutputEntity> outputs, String groupBy,
                             MovementsDashboardDto summaryMovements) {
        List<GroupMovementsDto> groups = groupMovementsService.getGroupMovements(entries, outputs, groupBy);
        summaryMovements.setGroupsMovements(groups);
    }

    /**
     * Calcula y asigna al ´summaryMovements´ los porcentajes de variación entre entradas y salidas.
     **/
    private void applyPercentages(MovementsDashboardDto summaryMovements, BigDecimal totalEntry, BigDecimal totalOutput) {
        // Si el total de salidas es 0 y el total de entradas tiene algo, se fija el `variationEntry` en 100%
        if (totalOutput.compareTo(BigDecimal.ZERO) == 0 && totalEntry.compareTo(BigDecimal.ZERO) > 0) {
            summaryMovements.setPercentageVariationEntry(BigDecimal.valueOf(100L));

            // Si el total de entradas es 0 y el total de salidas tiene algo, se fija el `variationOutput` en 100%
        } else if (totalEntry.compareTo(BigDecimal.ZERO) == 0 && totalOutput.compareTo(BigDecimal.ZERO) > 0) {
            summaryMovements.setPercentageVariationOutput(BigDecimal.valueOf(100L));

            // Si hay entradas y salidas se calcula
        } else if (totalEntry.compareTo(BigDecimal.ZERO) > 0 && totalOutput.compareTo(BigDecimal.ZERO) > 0){

            // % variación entradas = (totalEntry / totalOutput - 1) * 100)
            summaryMovements.setPercentageVariationEntry(
                    totalEntry
                            .divide(totalOutput, 2, RoundingMode.HALF_UP)
                            .subtract(BigDecimal.ONE)
                            .multiply(BigDecimal.valueOf(100L))
            );

            // % variación salidas = (totalOutput / totalOutput - 1) * 100)
            summaryMovements.setPercentageVariationOutput(
                    totalOutput
                            .divide(totalEntry, 2, RoundingMode.HALF_UP)
                            .subtract(BigDecimal.ONE)
                            .multiply(BigDecimal.valueOf(100L))
            );
        } else {
            summaryMovements.setPercentageVariationEntry(BigDecimal.ZERO);
            summaryMovements.setPercentageVariationOutput(BigDecimal.ZERO);
        }
    }

    /**
     * Calcula y asigna al ´summaryMovements´ los promedios de entradas y salidas por día.
     **/
    private void applyDailyAverages(MovementsDashboardDto summaryMovements, LocalDate startDate, LocalDate endDate) {
        // Calcula diferencia de dias entre startDate y endDate (inclusive)
        long daysBetweenLong = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        BigDecimal days = BigDecimal.valueOf(daysBetweenLong);

        // Calcular promedios
        BigDecimal avgEntries = summaryMovements.getTotalEntry().divide(days, 2, RoundingMode.HALF_UP);
        BigDecimal avgOutputs = summaryMovements.getTotalOutput().divide(days, 2, RoundingMode.HALF_UP);

        summaryMovements.setAvgEntryDay(avgEntries);
        summaryMovements.setAvgOutputDay(avgOutputs);
    }

    /**
     * Calcula y asigna al ´summaryMovements´ los promedios de entradas y salidas por semana.
     **/
    private void applyWeeklyAverages(MovementsDashboardDto summaryMovements, LocalDate startDate, LocalDate endDate) {
        // Calcula días entre startDate y endDate
        long daysBetweenLong = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        // Calcula semanas entre startDate y endDate (Math.ceil redondea para arriba, para que siempre haya un entero)
        long weeksLong = (long) Math.ceil(daysBetweenLong / 7.0);
        BigDecimal weeks = BigDecimal.valueOf(weeksLong);

        // Calcular promedios
        BigDecimal avgEntryWeek = summaryMovements.getTotalEntry().divide(weeks, 2, RoundingMode.HALF_UP);
        BigDecimal avgOutputWeek = summaryMovements.getTotalOutput().divide(weeks, 2, RoundingMode.HALF_UP);

        summaryMovements.setAvgEntryWeek(avgEntryWeek);
        summaryMovements.setAvgOutputWeek(avgOutputWeek);
    }

    /**
     * Calcula y asigna al ´summaryMovements´ los entradas (compradas y recibidas por donación) y salidas totales.
     **/
    private void applyTotalQuantities(List<EntryEntity> entries,
                                      List<OutputEntity> outputs,
                                      MovementsDashboardDto summaryMovements) {
        for (EntryEntity e : entries) {
            if (e.getEntryType() == EntryType.PURCHASE) {
                summaryMovements.setEntryPurchaseQty(summaryMovements.getEntryPurchaseQty().add(e.getQuantity()));
            } else {
                summaryMovements.setEntryDonationQty(summaryMovements.getEntryDonationQty().add(e.getQuantity()));
            }
        }
        for (OutputEntity o : outputs) {
            summaryMovements.setTotalOutput(summaryMovements.getTotalOutput().add(o.getQuantity()));
        }
        summaryMovements.setTotalEntry(
                summaryMovements.getEntryPurchaseQty().add(summaryMovements.getEntryDonationQty())
        );
    }
}