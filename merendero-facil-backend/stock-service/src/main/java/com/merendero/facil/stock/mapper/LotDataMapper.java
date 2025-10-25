package com.merendero.facil.stock.mapper;

import com.merendero.facil.movement.entity.EntryEntity;
import com.merendero.facil.stock.dto.LotDto;
import com.merendero.facil.stock.entity.SupplyLotEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * La clase "LotDataMapper" se encarga de mapear y crear lotes de insumos.
 */
@Component
public class LotDataMapper {
    /**
     * Crea una entidad {@link SupplyLotEntity} a partir del dto de petición {@link EntryRequestDto}.
     */
    public SupplyLotEntity createLotFromEntryRequest(EntryEntity entryEntity, LocalDate expirationDate) {
        return SupplyLotEntity.builder()
                .entry(entryEntity)
                .initialQuantity(entryEntity.getQuantity())
                .currentQuantity(entryEntity.getQuantity())
                .expirationDate(expirationDate)
                .notified(false)
                .build();
    }

    /**
     * Crea un dto {@link LotDto} a partir de la entidad {@link SupplyLotEntity}.
     */
    public LotDto createLotDtoFromLotEntity(SupplyLotEntity lotEntity) {
        return LotDto.builder()
                .id(lotEntity.getId())
                .initialQuantity(lotEntity.getInitialQuantity())
                .currentQuantity(lotEntity.getCurrentQuantity())
                .expirationDate(lotEntity.getExpirationDate())
                .daysToExpire(-1)
                .build();
    }
}