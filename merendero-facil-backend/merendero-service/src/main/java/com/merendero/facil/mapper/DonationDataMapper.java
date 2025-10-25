package com.merendero.facil.mapper;

import com.merendero.facil.dto.donation.DonationRequestDto;
import com.merendero.facil.dto.donation.DonationResponseDto;
import com.merendero.facil.entities.DonationEntity;
import com.merendero.facil.entities.MerenderoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper component para convertir entre entidades Donation y DTOs
 * Maneja las transformaciones de datos para las donaciones
 */
@Component
public class DonationDataMapper {
    /**
     * Mapea una entidad Donation a su DTO de respuesta
     **/
    public DonationResponseDto mapDonationEntityToDonationResponse(DonationEntity donationEntity) {
        return DonationResponseDto.builder()
                .id(donationEntity.getId())
                .userEmail(donationEntity.getUserEmail())
                .userId(donationEntity.getUserId())
                .donationDate(donationEntity.getDonationDate())
                .merenderoId(donationEntity.getMerendero().getId())

                .paymentId(donationEntity.getPaymentId())
                .grossAmount(donationEntity.getGrossAmount())
                .mpFee(donationEntity.getMpFee())
                .netAmount(donationEntity.getNetAmount())

                .build();
    }

    /**
     * Convierte una lista de entidades Donation a lista de DTOs de respuesta
     **/
    public List<DonationResponseDto> mapDonationEntitiesToDonationResponses
            (List<DonationEntity> donationEntities) {
        List<DonationResponseDto> responses = new ArrayList<>();
        for (DonationEntity dE : donationEntities){
            responses.add(this.mapDonationEntityToDonationResponse(dE));
        }
        return responses;
    }

    /**
     * Convierte DTO de solicitud Donation a entidad, incluyendo relaciones
     **/
    public DonationEntity mapDonationRequestToDonationEntity(DonationRequestDto donationRequestDto,
                                                             Long userId, MerenderoEntity merenderoEntity) {
        return DonationEntity.builder()
                .userEmail(donationRequestDto.getUserEmail())
                .userId(userId)
                .merendero(merenderoEntity)
                .donationDate(donationRequestDto.getDonationDate())

                .paymentId(donationRequestDto.getPaymentId())
                .grossAmount(donationRequestDto.getGrossAmount())
                .mpFee(donationRequestDto.getMpFee())
                .netAmount(donationRequestDto.getNetAmount())

                .build();
    }

    /**
     * Convierte una página de entidades Donation a página de DTOs de respuesta
     **/
    public Page<DonationResponseDto> mapDonationEntitiesPageToDonationResponsesPage(Page<DonationEntity> donationPagesEntities) {
        List<DonationResponseDto> donationResponses = mapDonationEntitiesToDonationResponses(donationPagesEntities.getContent());
        return new PageImpl<>(
                donationResponses,
                donationPagesEntities.getPageable(),
                donationPagesEntities.getTotalElements()
        );
    }
}