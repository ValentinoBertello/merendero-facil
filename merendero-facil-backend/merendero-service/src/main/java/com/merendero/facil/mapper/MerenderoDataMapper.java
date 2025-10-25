package com.merendero.facil.mapper;

import com.merendero.facil.dto.merendero.MerenderoRequestDto;
import com.merendero.facil.dto.merendero.MerenderoResponseDto;
import com.merendero.facil.entities.MerenderoEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * La clase "MerenderoDataMapper" se encarga de mapear merenderos "request", "entity" y "response"
 * entre ellos.
 */
@Component
public class MerenderoDataMapper {

    /**
     * Mapea una entidad de merendero {@link MerenderoEntity} a su DTO de respuesta {@link MerenderoResponseDto}.
     */
    public MerenderoResponseDto mapMerenderoEntityToMerenderoResponse(MerenderoEntity merenderoEntity) {
        return MerenderoResponseDto.builder()
                .id(merenderoEntity.getId())
                .name(merenderoEntity.getName())
                .address(merenderoEntity.getAddress())
                .latitude(merenderoEntity.getLatitude())
                .longitude(merenderoEntity.getLongitude())
                .capacity(merenderoEntity.getCapacity())
                .daysOpen(merenderoEntity.getDaysOpen())
                .openingTime(merenderoEntity.getOpeningTime())
                .accessToken(merenderoEntity.getAccessToken())
                .closingTime(merenderoEntity.getClosingTime())
                .managerId(merenderoEntity.getManagerId())
                .managerEmail(merenderoEntity.getManagerEmail())
                .active(merenderoEntity.getActive())
                .build();
    }

    /**
     * Mapea una lista de entidades de merendero {@link MerenderoEntity} a una lista de DTOs de respuesta.
     */
    public List<MerenderoResponseDto> mapMerenderoEntitiesToMerenderoResponses
            (List<MerenderoEntity> merenderoEntities) {
        List<MerenderoResponseDto> responses = new ArrayList<>();
        for (MerenderoEntity mE : merenderoEntities){
            responses.add(this.mapMerenderoEntityToMerenderoResponse(mE));
        }
        return responses;
    }

    /**
     * Mapea un DTO de petición {@link MerenderoRequestDto} a una nueva entidad {@link MerenderoEntity}.
     */
    public MerenderoEntity mapMerenderoRequestToMerenderoEntity(MerenderoRequestDto merenderoRequestDto,
                                                                Long managerId) {
        return MerenderoEntity.builder()
                .name(merenderoRequestDto.getName())
                .address(merenderoRequestDto.getAddress())
                .latitude(merenderoRequestDto.getLatitude())
                .longitude(merenderoRequestDto.getLongitude())
                .capacity(merenderoRequestDto.getCapacity())
                .daysOpen(merenderoRequestDto.getDaysOpen())
                .openingTime(merenderoRequestDto.getOpeningTime())
                .closingTime(merenderoRequestDto.getClosingTime())
                .managerId(managerId)
                .managerEmail(merenderoRequestDto.getManagerEmail())
                .active(true)
                .createdUser(merenderoRequestDto.getCreatedUser())
                .lastUpdatedUser(merenderoRequestDto.getCreatedUser())
                .build();
    }
}
