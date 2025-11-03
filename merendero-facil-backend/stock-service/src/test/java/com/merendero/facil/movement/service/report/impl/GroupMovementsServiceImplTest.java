package com.merendero.facil.movement.service.report.impl;

import com.merendero.facil.movement.dto.report.GroupMovementsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static com.merendero.facil.helper.MovementTestHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GroupMovementsServiceImplTest {

    @InjectMocks
    private GroupMovementsServiceImpl groupMovementsService;

    @Test
    void getGroupMovementsPerDay() {
        List<GroupMovementsDto> groups = groupMovementsService.getGroupMovements(ALL_ENTRIES, ALL_OUTPUTS, "day");
        assertEquals(4, groups.size());

        assertEquals(BigDecimal.valueOf(10), groups.get(0).getEntryPurchaseQty());
        assertEquals(BigDecimal.valueOf(4), groups.get(0).getOutputQty());

        assertEquals(BigDecimal.valueOf(0), groups.get(1).getEntryPurchaseQty());
        assertEquals(BigDecimal.valueOf(0), groups.get(1).getEntryDonationQty());
        assertEquals(BigDecimal.valueOf(1), groups.get(1).getOutputQty());
    }

    @Test
    void getGroupMovementsPerMonth() {
        List<GroupMovementsDto> groups = groupMovementsService.getGroupMovements(ALL_ENTRIES, ALL_OUTPUTS, "month");
        assertEquals(1, groups.size());

        assertEquals(BigDecimal.valueOf(5.5), groups.get(0).getEntryDonationQty());
        assertEquals(BigDecimal.valueOf(20), groups.get(0).getEntryPurchaseQty());
        assertEquals(BigDecimal.valueOf(14.5), groups.get(0).getOutputQty());
    }

    @Test
    void getGroupMovementsPerWeek() {
        List<GroupMovementsDto> groups = groupMovementsService.getGroupMovements(ALL_ENTRIES, ALL_OUTPUTS, "week");
        assertEquals(2, groups.size());

        // Semana del lunes 26/05/2025
        assertEquals(BigDecimal.valueOf(10), groups.get(0).getEntryPurchaseQty());
        assertEquals(BigDecimal.valueOf(4), groups.get(0).getOutputQty());

        // Semana del lunes 26/05/2025
        assertEquals(BigDecimal.valueOf(10), groups.get(1).getEntryPurchaseQty());
        assertEquals(BigDecimal.valueOf(10.5), groups.get(1).getOutputQty());
    }

    @Test
    void getGroupMovementsOnlyEntries() {
        // Solo entradas
        List<GroupMovementsDto> groups = groupMovementsService.getGroupMovements(ALL_ENTRIES, Collections.emptyList(), "day");

        // Verificar que la cantidad de grupos sea correcta según fechas de entradas
        assertEquals(3, groups.size()); // 01/06, 03/06, 08/06

        // Primer grupo: 01/06
        assertEquals(LocalDate.of(2025, 6, 1), groups.get(0).getDate());
        assertEquals(new BigDecimal("10"), groups.get(0).getEntryPurchaseQty());
        assertEquals(new BigDecimal("5.5"), groups.get(0).getEntryDonationQty());
        assertEquals(BigDecimal.ZERO, groups.get(0).getOutputQty());

        // Segundo grupo: 03/06
        assertEquals(LocalDate.of(2025, 6, 3), groups.get(1).getDate());
        assertEquals(new BigDecimal("3"), groups.get(1).getEntryPurchaseQty());
        assertEquals(BigDecimal.ZERO, groups.get(1).getEntryDonationQty());
        assertEquals(BigDecimal.ZERO, groups.get(1).getOutputQty());

        // Tercer grupo: 08/06
        assertEquals(LocalDate.of(2025, 6, 8), groups.get(2).getDate());
        assertEquals(new BigDecimal("7"), groups.get(2).getEntryPurchaseQty());
        assertEquals(BigDecimal.ZERO, groups.get(2).getEntryDonationQty());
        assertEquals(BigDecimal.ZERO, groups.get(2).getOutputQty());
    }
}