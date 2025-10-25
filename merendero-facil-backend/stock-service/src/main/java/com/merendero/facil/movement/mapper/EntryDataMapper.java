package com.merendero.facil.movement.mapper;

import com.merendero.facil.movement.dto.entry.EntryRequestDto;
import com.merendero.facil.movement.dto.entry.EntryResponseDto;
import com.merendero.facil.movement.dto.entry.EntryType;
import com.merendero.facil.movement.entity.EntryEntity;
import com.merendero.facil.supply.entity.SupplyEntity;
import org.springframework.stereotype.Component;

/**
 * La clase "EntryDataMapper" se encarga de mapear entries "request", "entity" y "response"
 * entre ellos.
 */
@Component
public class EntryDataMapper {

    /**
     * Mapea una entidad {@link EntryEntity} a un dto de respuesta {@link EntryResponseDto}.
     */
    public EntryResponseDto mapEntryEntityToEntryResponse(EntryEntity entity) {
        return EntryResponseDto.builder()
                .id(entity.getId())
                .supplyId(entity.getSupply().getId())
                .supplyName(entity.getSupply().getName())
                .category(entity.getSupply().getSupplyCategory().getName())
                .unit(entity.getSupply().getUnit().name())
                .quantity(entity.getQuantity())
                .entryDate(entity.getEntryDate())
                .entryType(entity.getEntryType().name())
                .build();
    }

    /**
     * Mapea un DTO de petición {@link EntryRequestDto} a una nueva entidad {@link EntryEntity}.
     */
    public EntryEntity mapEntryRequestToEntryEntity(EntryRequestDto requestDto, SupplyEntity supplyEntity) {
        return EntryEntity.builder()
                .supply(supplyEntity)
                .quantity(requestDto.getQuantity())
                .entryType(EntryType.valueOf(requestDto.getEntryType().name()))
                .build();
    }
}
