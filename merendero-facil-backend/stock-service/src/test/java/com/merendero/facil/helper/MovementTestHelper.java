package com.merendero.facil.helper;


import com.merendero.facil.movement.dto.entry.EntryType;
import com.merendero.facil.movement.entity.EntryEntity;
import com.merendero.facil.movement.entity.OutputEntity;
import com.merendero.facil.supply.entity.SupplyEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MovementTestHelper {

    // Supply de ejemplo
    public static final SupplyEntity SUPPLY_1 = SupplyEntity.builder()
            .id(1L)
            .name("Pan")
            .merenderoId(1L)
            .minQuantity(new BigDecimal("10"))
            .lastAlertDate(LocalDate.now().minusDays(1))
            .build();

    // ------- Entradas (EntryEntity) -------
    public static final EntryEntity ENTRY_1 = EntryEntity.builder()
            .id(1L)
            .supply(SUPPLY_1)
            .quantity(new BigDecimal("10"))
            .entryDate(LocalDateTime.of(2025, 6, 1, 10, 0)) // 01/06/2025
            .entryType(EntryType.PURCHASE)
            .build();

    public static final EntryEntity ENTRY_2 = EntryEntity.builder()
            .id(2L)
            .supply(SUPPLY_1)
            .quantity(new BigDecimal("5.5"))
            .entryDate(LocalDateTime.of(2025, 6, 1, 15, 30)) // 01/06/2025
            .entryType(EntryType.DONATION)
            .build();

    public static final EntryEntity ENTRY_3 = EntryEntity.builder()
            .id(3L)
            .supply(SUPPLY_1)
            .quantity(new BigDecimal("3"))
            .entryDate(LocalDateTime.of(2025, 6, 3, 9, 0)) // 03/06/2025
            .entryType(EntryType.PURCHASE)
            .build();

    public static final EntryEntity ENTRY_4 = EntryEntity.builder()
            .id(4L)
            .supply(SUPPLY_1)
            .quantity(new BigDecimal("7"))
            .entryDate(LocalDateTime.of(2025, 6, 8, 11, 45)) // 08/06/2025
            .entryType(EntryType.PURCHASE)
            .build();

    public static final List<EntryEntity> ALL_ENTRIES = List.of(
            ENTRY_1, ENTRY_2, ENTRY_3, ENTRY_4
    );

    // ------- Salidas (OutputEntity) -------
    public static final OutputEntity OUTPUT_1 = OutputEntity.builder()
            .id(1L)
            .supply(SUPPLY_1)
            .quantity(new BigDecimal("4"))
            .outputDate(LocalDateTime.of(2025, 6, 1, 12, 0)) // 01/06/2025
            .build();

    public static final OutputEntity OUTPUT_2 = OutputEntity.builder()
            .id(2L)
            .supply(SUPPLY_1)
            .quantity(new BigDecimal("1"))
            .outputDate(LocalDateTime.of(2025, 6, 2, 11, 15)) // 02/06/2025
            .build();

    public static final OutputEntity OUTPUT_3 = OutputEntity.builder()
            .id(3L)
            .supply(SUPPLY_1)
            .quantity(new BigDecimal("6.5"))
            .outputDate(LocalDateTime.of(2025, 6, 3, 18, 0)) // 03/06/2025
            .build();

    public static final OutputEntity OUTPUT_4 = OutputEntity.builder()
            .id(4L)
            .supply(SUPPLY_1)
            .quantity(new BigDecimal("3"))
            .outputDate(LocalDateTime.of(2025, 6, 8, 9, 30)) // 08/06/2025
            .build();

    public static final List<OutputEntity> ALL_OUTPUTS = List.of(
            OUTPUT_1, OUTPUT_2, OUTPUT_3, OUTPUT_4
    );
}