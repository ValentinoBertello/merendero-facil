package com.merendero.facil.service;

import com.merendero.facil.dto.donation.DonationRequestDto;
import com.merendero.facil.dto.donation.DonationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface DonationService {

    void saveDonation(DonationRequestDto request);

    List<DonationResponseDto> getDonationByMerenderoId(Long merenderoId);

    Page<DonationResponseDto> getDonationPagesByFilters(Long merenderoId, LocalDate sinceDate, LocalDate untilDate,
                                                        String donorEmail, Pageable pageable);
}
