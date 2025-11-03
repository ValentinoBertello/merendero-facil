package com.merendero.facil.helper;


import com.merendero.facil.common.enums.Unit;
import com.merendero.facil.supply.entity.SupplyCategoryEntity;
import com.merendero.facil.supply.entity.SupplyEntity;

import java.math.BigDecimal;

public class SupplyTestHelper {
    // SupplyCategoryEntity mock
    public static final SupplyCategoryEntity CATEGORY_ALIMENTOS = SupplyCategoryEntity.builder()
            .id(1L)
            .name("Alimentos")
            .build();

    // SupplyEntity mocks
    public static final SupplyEntity SUPPLY_ARROZ = SupplyEntity.builder()
            .id(1L)
            .name("Arroz")
            .unit(Unit.KG)
            .minQuantity(new BigDecimal("10"))
            .active(true)
            .merenderoId(1L)
            .supplyCategory(CATEGORY_ALIMENTOS)
            .build();

    public static final SupplyEntity SUPPLY_LECHE = SupplyEntity.builder()
            .id(2L)
            .name("Leche")
            .unit(Unit.LITRO)
            .minQuantity(new BigDecimal("5"))
            .active(true)
            .merenderoId(1L)
            .supplyCategory(CATEGORY_ALIMENTOS)
            .build();

    public static final SupplyEntity SUPPLY_FIDEOS = SupplyEntity.builder()
            .id(3L)
            .name("Fideos")
            .unit(Unit.KG)
            .minQuantity(new BigDecimal("8"))
            .active(true)
            .merenderoId(1L)
            .supplyCategory(CATEGORY_ALIMENTOS)
            .build();
}
