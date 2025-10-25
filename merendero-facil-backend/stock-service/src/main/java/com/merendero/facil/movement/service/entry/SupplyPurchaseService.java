package com.merendero.facil.movement.service.entry;


import com.merendero.facil.movement.dto.purchase.PurchaseRequestDto;
import com.merendero.facil.movement.dto.purchase.PurchaseResponseDto;

public interface SupplyPurchaseService {

    PurchaseResponseDto createPurchase(PurchaseRequestDto dto);
}
