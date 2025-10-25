package com.merendero.facil.service.impl;

import com.merendero.facil.clients.UserRestTemplate;
import com.merendero.facil.dto.donation.DonationRequestDto;
import com.merendero.facil.dto.donation.DonationResponseDto;
import com.merendero.facil.entities.DonationEntity;
import com.merendero.facil.entities.MerenderoEntity;
import com.merendero.facil.mapper.DonationDataMapper;
import com.merendero.facil.repository.DonationRepository;
import com.merendero.facil.repository.MerenderoRepository;
import com.merendero.facil.repository.specifications.DonationSpecifications;
import com.merendero.facil.service.DonationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Toda la lógica de negocio referida a las donaciones.
 **/
@Service
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;
    private final DonationDataMapper donationDataMapper;
    private final UserRestTemplate userRestTemplate;
    private final MerenderoRepository merenderoRepository;

    public DonationServiceImpl(DonationRepository donationRepository, DonationDataMapper donationDataMapper, UserRestTemplate userRestTemplate, MerenderoRepository merenderoRepository) {
        this.donationRepository = donationRepository;
        this.donationDataMapper = donationDataMapper;
        this.userRestTemplate = userRestTemplate;
        this.merenderoRepository = merenderoRepository;
    }

    /**
     * Guarda una donación en la base de datos después de un pago exitoso en Mercado Pago.
     *
     * Este método NO es un endpoint de API expuesto directamente, sino parte del flujo interno
     * de procesamiento de pagos. Ejecutándose en el endpoint /mercado-pago/notification.
     */
    @Override
    @Transactional(readOnly = false)
    public void saveDonation(DonationRequestDto request) {
        this.checkForDuplicatePayment(request.getPaymentId());
        Long userId = this.userRestTemplate.getUserByEmail(request.getUserEmail()).getId();
        MerenderoEntity merenderoEntity = this.merenderoRepository.findByIdAndActiveTrue(request.getMerenderoId()).get();
        DonationEntity donationEntity = this.donationDataMapper.mapDonationRequestToDonationEntity(request,userId, merenderoEntity);
        donationEntity = this.donationRepository.save(donationEntity);
        this.donationDataMapper.mapDonationEntityToDonationResponse(donationEntity);
    }

    /**
     * Obtiene todas las donaciones asociadas a un merendero específico.
     * Permitiendo acceder solo al manager del merendero
     */
    @Override
    @Transactional(readOnly = true)
    public List<DonationResponseDto> getDonationByMerenderoId(Long merenderoId) {
        List<DonationEntity> donationEntities = this.donationRepository.findByMerenderoId(merenderoId);
        return this.donationDataMapper.mapDonationEntitiesToDonationResponses(donationEntities);
    }

    /**
     * Obtiene donaciones paginadas aplicando filtros por merendero, fechas y email del donante
     */
    @Override
    public Page<DonationResponseDto> getDonationPagesByFilters(Long merenderoId, LocalDate sinceDate,
                                                               LocalDate untilDate, String donorEmail, Pageable pageable) {
        Page<DonationEntity> donationPagesEntities = this.donationRepository.findAll(
                DonationSpecifications.donationSearch(merenderoId, sinceDate, untilDate, donorEmail), pageable
        );
        return this.donationDataMapper.mapDonationEntitiesPageToDonationResponsesPage(donationPagesEntities);
    }

    /**
     * Verifica si ya existe una donación con el mismo paymentId para prevenir duplicados.
     */
    private void checkForDuplicatePayment(String paymentId) {
        if (donationRepository.existsByPaymentId(paymentId)) {
            throw new RuntimeException("Donación ya registrada con paymentId: " + paymentId);
        }
    }
}