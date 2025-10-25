package com.merendero.facil.stock.service;


import com.merendero.facil.movement.entity.EntryEntity;
import com.merendero.facil.movement.entity.OutputEntity;
import com.merendero.facil.stock.dto.ItemStockDto;
import com.merendero.facil.stock.dto.LotDto;
import com.merendero.facil.stock.entity.SupplyLotEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface LotService {
    SupplyLotEntity createLotFromEntry(EntryEntity entryEntity, LocalDate expirationDate);

    void deductFromLots(OutputEntity outputEntity);

    BigDecimal getTotalStockBySupply(Long merenderoId, Long supplyId);

    List<LotDto> getLotsBySupply(Long merenderoId, Long supplyId);

    List<ItemStockDto> getStockItems(Long merenderoId);

    Boolean checkSupplyStock(Long merenderoId, Long supplyId, BigDecimal quantity);
}
