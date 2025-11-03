package com.merendero.facil.movement.service.report.impl;

import com.merendero.facil.movement.dto.report.MovementsDashboardDto;
import com.merendero.facil.movement.repository.EntryRepository;
import com.merendero.facil.movement.repository.OutputRepository;
import com.merendero.facil.movement.service.report.GroupMovementsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;

import static com.merendero.facil.helper.MovementTestHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovementsDashboardServiceImplTest {

    @Mock
    private GroupMovementsService groupMovementsService;
    @Mock
    private EntryRepository entryRepository;
    @Mock
    private OutputRepository outputRepository;

    @InjectMocks
    private MovementsDashboardServiceImpl movementsReportService;

    @Test
    void getSummaryMovements() {
        LocalDateTime since = LocalDate.of(2025, 6, 1).atStartOfDay();
        LocalDateTime until = LocalDate.of(2025, 6, 8).atTime(LocalTime.MAX);

        when(entryRepository.findByDatesAndSupply(since, until, 1L)).thenReturn(ALL_ENTRIES);
        when(outputRepository.findByDatesAndSupply(since, until, 1L)).thenReturn(ALL_OUTPUTS);

        when(groupMovementsService.getGroupMovements(ALL_ENTRIES, ALL_OUTPUTS, "day"))
                .thenReturn(Collections.emptyList());

        MovementsDashboardDto summary = movementsReportService.getSummaryMovements(
                1L,
                LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 6, 8),
                "day");

        // Totales
        assertEquals(new BigDecimal("20.00"), summary.getEntryPurchaseQty());
        assertEquals(new BigDecimal("5.50"), summary.getEntryDonationQty());
        assertEquals(new BigDecimal("25.50"), summary.getTotalEntry());
        assertEquals(new BigDecimal("14.50"), summary.getTotalOutput());

        // Promedios
        assertEquals(new BigDecimal("3.19"), summary.getAvgEntryDay());
        assertEquals(new BigDecimal("1.81"), summary.getAvgOutputDay());

        assertEquals(new BigDecimal("12.75"), summary.getAvgEntryWeek());
        assertEquals(new BigDecimal("7.25"), summary.getAvgOutputWeek());

        // Variación porcentaje
        assertEquals(new BigDecimal("76.00"), summary.getPercentageVariationEntry());
        assertEquals(new BigDecimal("-43.00"), summary.getPercentageVariationOutput());
    }

    @Test
    void getSummaryMovementsEmpty() {
        LocalDateTime since = LocalDate.of(2025, 6, 1).atStartOfDay();
        LocalDateTime until = LocalDate.of(2025, 6, 8).atTime(LocalTime.MAX);

        // Mockear repositorios para que devuelvan vacío
        when(entryRepository.findByDatesAndSupply(since, until, 1L)).thenReturn(Collections.emptyList());
        when(outputRepository.findByDatesAndSupply(since, until, 1L)).thenReturn(Collections.emptyList());

        // Llamada al service
        MovementsDashboardDto summary = movementsReportService.getSummaryMovements(
                1L,
                LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 6, 8),
                "day");

        // Comprobamos que todos los totales sean cero
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), summary.getTotalEntry());
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), summary.getTotalOutput());

        // Grupos
        assertTrue(summary.getGroupsMovements().isEmpty());
    }

    @Test
    void getSummaryMovementsOnlyEntries() {
        LocalDateTime since = LocalDate.of(2025, 6, 1).atStartOfDay();
        LocalDateTime until = LocalDate.of(2025, 6, 8).atTime(LocalTime.MAX);

        // Solo entradas
        when(entryRepository.findByDatesAndSupply(since, until, 1L)).thenReturn(ALL_ENTRIES);
        when(outputRepository.findByDatesAndSupply(since, until, 1L)).thenReturn(Collections.emptyList());

        // GroupMovementsService mock (si no lo usamos para este test, puede devolver vacío)
        when(groupMovementsService.getGroupMovements(ALL_ENTRIES, Collections.emptyList(), "day"))
                .thenReturn(Collections.emptyList());

        MovementsDashboardDto summary = movementsReportService.getSummaryMovements(
                1L,
                LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 6, 8),
                "day");

        // Totales
        assertEquals(new BigDecimal("20.00"), summary.getEntryPurchaseQty());
        assertEquals(new BigDecimal("5.50"), summary.getEntryDonationQty());
        assertEquals(new BigDecimal("25.50"), summary.getTotalEntry());
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), summary.getTotalOutput());

        // Variación porcentual
        assertEquals(new BigDecimal("100"), summary.getPercentageVariationEntry());
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), summary.getPercentageVariationOutput());
    }
}