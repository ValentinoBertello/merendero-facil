package com.merendero.facil.supply.mapper;

import com.merendero.facil.common.enums.Unit;
import com.merendero.facil.supply.dto.SupplyRequestDto;
import com.merendero.facil.supply.dto.SupplyResponseDto;
import com.merendero.facil.supply.entity.SupplyCategoryEntity;
import com.merendero.facil.supply.entity.SupplyEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * La clase "SupplyDataMapper" se encarga de mapear insumos "request", "entity" y "response"
 * entre ellos.
 */
@Component
public class SupplyDataMapper {
    /**
     * Mapea una entidad de insumo {@link SupplyEntity} a su DTO de respuesta {@link SupplyResponseDto}.
     */
    public SupplyResponseDto mapSupplyEntityToSupplyResponse(SupplyEntity supplyEntity) {
        return SupplyResponseDto.builder()
                .id(supplyEntity.getId())
                .name(supplyEntity.getName())
                .unit(supplyEntity.getUnit().name())
                .minQuantity(supplyEntity.getMinQuantity())
                .merenderoId(supplyEntity.getMerenderoId())
                .categoryId(supplyEntity.getSupplyCategory().getId())
                .build();
    }

    /**
     * Mapea una lista de entidades de insumo {@link SupplyEntity} a una lista de DTOs de respuesta.
     */
    public List<SupplyResponseDto> mapSupplyEntitiesToSupplyResponses
            (List<SupplyEntity> supplyEntities) {
        List<SupplyResponseDto> responses = new ArrayList<>();
        for (SupplyEntity sE : supplyEntities){
            responses.add(this.mapSupplyEntityToSupplyResponse(sE));
        }
        return responses;
    }

    /**
     * Mapea un DTO de petición {@link SupplyRequestDto} a una nueva entidad {@link SupplyEntity}.
     */
    public SupplyEntity mapSupplyRequestToSupplyEntity(SupplyRequestDto supplyRequestDto,
                                                       SupplyCategoryEntity category,
                                                       Long merenderoId) {
        return SupplyEntity.builder()
                .name(supplyRequestDto.getName())
                .unit(Unit.valueOf(supplyRequestDto.getUnit()))
                .minQuantity(supplyRequestDto.getMinQuantity())
                .lastAlertDate(supplyRequestDto.getLastAlertDate())
                .active(true)
                .supplyCategory(category)
                .merenderoId(merenderoId)
                .build();
    }
}