package com.merendero.facil.helper;

import com.merendero.facil.stock.entity.SupplyLotEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class SupplyLotHelper {

    public static final SupplyLotEntity LOT_1 = SupplyLotEntity.builder()
            .id(1L)
            .entry(MovementTestHelper.ENTRY_1)
            .initialQuantity(new BigDecimal("100"))

            .currentQuantity(new BigDecimal("80")) // current = 80

            .expirationDate(LocalDate.now().plusDays(30))
            .notified(false)
            .build();

    public static final SupplyLotEntity LOT_2 = SupplyLotEntity.builder()
            .id(2L)
            .entry(MovementTestHelper.ENTRY_1)
            .initialQuantity(new BigDecimal("50"))

            .currentQuantity(new BigDecimal("10")) // current = 10

            .expirationDate(LocalDate.now().plusDays(10))
            .notified(true)
            .build();

    public static final SupplyLotEntity LOT_3 = SupplyLotEntity.builder()
            .id(3L)
            .entry(MovementTestHelper.ENTRY_1)
            .initialQuantity(new BigDecimal("200"))

            .currentQuantity(new BigDecimal("200")) // current = 200

            .expirationDate(LocalDate.now().plusMonths(2))
            .notified(false)
            .build();

    public static final SupplyLotEntity LOT_4 = SupplyLotEntity.builder()
            .id(5L)
            .entry(MovementTestHelper.ENTRY_1)
            .initialQuantity(new BigDecimal("75"))

            .currentQuantity(new BigDecimal("50")) // current = 50

            .expirationDate(LocalDate.now().plusDays(60))
            .notified(false)
            .build();

    // Total currentQuantity = 340
    public static final List<SupplyLotEntity> LOTS = List.of(
            LOT_2, // vence primero
            LOT_1,
            LOT_4,
            LOT_3  // vence más tarde
    );

}