package com.merendero.facil.movement.mapper;

import com.merendero.facil.movement.dto.output.OutputRequestDto;
import com.merendero.facil.movement.dto.output.OutputResponseDto;
import com.merendero.facil.movement.entity.OutputEntity;
import com.merendero.facil.supply.entity.SupplyEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * La clase "OutputDataMapper" se encarga de mapear salidas de insumos "request", "entity" y "response"
 * entre ellos.
 */
@Component
public class OutputDataMapper {
    /**
     * Mapea una entidad {@link OutputEntity} a un dto de respuesta {@link OutputResponseDto}.
     */
    public OutputResponseDto mapOutputEntityToOutputResponse(OutputEntity entity) {
        return OutputResponseDto.builder()
                .id(entity.getId())
                .supplyId(entity.getSupply().getId())
                .supplyName(entity.getSupply().getName())
                .category(entity.getSupply().getSupplyCategory().getName())
                .unit(entity.getSupply().getUnit().name())
                .quantity(entity.getQuantity())
                .outputDate(entity.getOutputDate())
                .build();
    }

    /**
     * Mapea una lista de entidades a una lista de dtos.
     */
    public List<OutputResponseDto> mapOutputEntitiesToOutputResponses
            (List<OutputEntity> entities) {
        List<OutputResponseDto> responses = new ArrayList<>();
        for (OutputEntity sE : entities){
            responses.add(this.mapOutputEntityToOutputResponse(sE));
        }
        return responses;
    }

    /**
     * Mapea un DTO de petición {@link OutputRequestDto} a una nueva entidad {@link OutputEntity}.
     */
    public OutputEntity mapOutputRequestToOutputEntity(OutputRequestDto requestDto, SupplyEntity supplyEntity) {
        return OutputEntity.builder()
                .supply(supplyEntity)
                .quantity(requestDto.getQuantity())
                .build();
    }
}