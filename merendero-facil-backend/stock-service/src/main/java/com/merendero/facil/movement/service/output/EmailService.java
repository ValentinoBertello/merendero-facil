package com.merendero.facil.movement.service.output;


import com.merendero.facil.supply.entity.SupplyEntity;

public interface EmailService {
    void sendLowStockEmail(SupplyEntity supply);
}
