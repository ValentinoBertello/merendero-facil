package com.merendero.facil.supply.service;


import com.merendero.facil.supply.dto.SupplyRequestDto;
import com.merendero.facil.supply.dto.SupplyResponseDto;
import com.merendero.facil.supply.entity.SupplyCategoryEntity;

import java.util.List;

public interface SupplyService {
    SupplyResponseDto saveSupply(Long merenderoId, SupplyRequestDto supplyRequestDto);

    List<SupplyResponseDto> getSuppliesFromMerendero(Long merenderoId);

    List<SupplyCategoryEntity> getSupplyCategories();

    Long removeSupplyFromMerendero(Long merenderoId, Long supplyId);
}
