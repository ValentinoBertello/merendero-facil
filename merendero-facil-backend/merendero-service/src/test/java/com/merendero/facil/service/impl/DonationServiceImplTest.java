package com.merendero.facil.service.impl;

import com.merendero.facil.clients.UserRestTemplate;
import com.merendero.facil.dto.apiExterna.UserResponse;
import com.merendero.facil.dto.donation.DonationResponseDto;
import com.merendero.facil.entities.DonationEntity;
import com.merendero.facil.helper.TestDonationHelper;
import com.merendero.facil.mapper.DonationDataMapper;
import com.merendero.facil.repository.DonationRepository;
import com.merendero.facil.repository.MerenderoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.merendero.facil.helper.TestDonationHelper.DONATION_1;
import static com.merendero.facil.helper.TestDonationHelper.DONATION_REQUEST_1;
import static com.merendero.facil.helper.TestMerenderoHelper.MERENDERO_1;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DonationServiceImplTest {
    @Mock
    private DonationRepository donationRepository;
    @Spy
    private DonationDataMapper donationDataMapper;
    @Mock
    private UserRestTemplate userRestTemplate;
    @Mock
    private MerenderoRepository merenderoRepository;

    @InjectMocks
    private DonationServiceImpl donationService;

    @Test
    void saveDonation() {
        when(donationRepository.existsByPaymentId(DONATION_REQUEST_1.getPaymentId()))
                .thenReturn(false);
        UserResponse userResponse = UserResponse.builder().id(1L).build();
        when(this.userRestTemplate.getUserByEmail(DONATION_REQUEST_1.getUserEmail()))
                .thenReturn(userResponse);
        when(this.merenderoRepository.findByIdAndActiveTrue(DONATION_REQUEST_1.getMerenderoId()))
                .thenReturn(Optional.ofNullable(MERENDERO_1));
        when(this.donationRepository.save(any())).thenReturn(DONATION_1);

        donationService.saveDonation(DONATION_REQUEST_1);

        verify(donationRepository).existsByPaymentId(DONATION_REQUEST_1.getPaymentId());
        verify(userRestTemplate).getUserByEmail(DONATION_REQUEST_1.getUserEmail());
        verify(merenderoRepository).findByIdAndActiveTrue(DONATION_REQUEST_1.getMerenderoId());
        verify(donationRepository).save(any(DonationEntity.class));
        verify(donationDataMapper).mapDonationEntityToDonationResponse(DONATION_1);
    }

    @Test
    void saveDonation_shouldThrowExceptionWhenDuplicatePayment() {
        when(donationRepository.existsByPaymentId(DONATION_REQUEST_1.getPaymentId()))
                .thenReturn(true);
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                donationService.saveDonation(DONATION_REQUEST_1)
        );
        assertEquals("Donación ya registrada con paymentId: " + DONATION_REQUEST_1.getPaymentId(),
                exception.getMessage());
    }

    @Test
    void getDonationByMerenderoId() {
        when(donationRepository.findByMerenderoId(1L))
                .thenReturn(List.of(TestDonationHelper.DONATION_1));
        DonationResponseDto dto = new DonationResponseDto();
        when(donationDataMapper.mapDonationEntitiesToDonationResponses(List.of(TestDonationHelper.DONATION_1)))
                .thenReturn(List.of(dto));

        List<DonationResponseDto> result = donationService.getDonationByMerenderoId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(dto, result.get(0));
    }

    @Test
    void getDonationPagesByFilters() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<DonationEntity> donationPage = new PageImpl<>(List.of(TestDonationHelper.DONATION_1));

        when(this.donationRepository.findAll(Mockito.<Specification<DonationEntity>>any(), any(Pageable.class)))
                .thenReturn(donationPage);

        Page<DonationResponseDto> result = this.donationService.getDonationPagesByFilters( 1L,
                LocalDate.now().minusDays(10), LocalDate.now(), "donante1@email.com", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(donationDataMapper).mapDonationEntitiesPageToDonationResponsesPage(donationPage);
    }
}