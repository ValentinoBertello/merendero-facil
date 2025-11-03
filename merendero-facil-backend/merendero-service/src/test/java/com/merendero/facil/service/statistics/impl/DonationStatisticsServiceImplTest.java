package com.merendero.facil.service.statistics.impl;

import com.merendero.facil.dto.donation.statistics.ChangeStats;
import com.merendero.facil.dto.donation.statistics.ComparisonStats;
import com.merendero.facil.dto.donation.statistics.DonationDashboardResponse;
import com.merendero.facil.entities.DonationEntity;
import com.merendero.facil.repository.DonationRepository;
import com.merendero.facil.service.statistics.DonationGroupingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.merendero.facil.helper.TestDonationHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DonationStatisticsServiceImplTest {
    @Mock
    private DonationRepository donationRepository;

    @Mock
    private DonationGroupingService donationGroupingService;

    @InjectMocks
    private DonationStatisticsServiceImpl donationStatisticsService;

    @Test
    void getDonationDashboard() {
        List<DonationEntity> donationRecurrentPeriod = List.of(
                DONATION_3, DONATION_4
        );
        List<DonationEntity> donationPreviousPeriod = List.of(
                DONATION_7, DONATION_8, DONATION_10, DONATION_1, DONATION_2
        );

        // Periodo actual: 16-31 de enero
        LocalDate start = LocalDate.of(2024, 1, 16);
        LocalDate end = LocalDate.of(2024, 1, 31);

        // Periodo anterior CALCULADO: 31 diciembre 2023 - 15 enero 2024
        LocalDate previousStart = LocalDate.of(2023, 12, 31);
        LocalDate previousEnd = LocalDate.of(2024, 1, 15);

        when(donationRepository.findByDatesAndMerendero(
                start.atStartOfDay(),
                end.atTime(LocalTime.MAX),
                1L)).thenReturn(donationRecurrentPeriod);

        when(donationRepository.findByDatesAndMerendero(
                previousStart.atStartOfDay(),
                previousEnd.atTime(LocalTime.MAX),
                1L)).thenReturn(donationPreviousPeriod);

        when(donationGroupingService.groupDonationsByPeriod(any(), any())).thenReturn(new ArrayList<>());

        Set<String> setEmails = donationPreviousPeriod.stream()
                .map(DonationEntity::getUserEmail)
                .collect(Collectors.toSet());

        when(donationRepository.findPreviousDonorEmails(start.atStartOfDay(), 1L)).thenReturn(setEmails);

        DonationDashboardResponse dashboardResult = this.donationStatisticsService.getDonationDashboard(
                start, end, "day", 1L
        );

        assertNotNull(dashboardResult);
        assertEquals(new BigDecimal("2500.00"), dashboardResult.getCurrentPeriod().getTotalAmountDonated());
        assertEquals(2, dashboardResult.getCurrentPeriod().getDonationCount());

        assertEquals(new BigDecimal("6300.00"), dashboardResult.getPreviousPeriod().getTotalAmountDonated());
        assertEquals(5, dashboardResult.getPreviousPeriod().getDonationCount());

        ComparisonStats comparisonStats = dashboardResult.getComparisonStats();
        ChangeStats amountChange = comparisonStats.getAmountDonatedChange();
        assertEquals(new BigDecimal("-3800.00"), amountChange.getValue());
        assertEquals(new BigDecimal("-60.3200"), amountChange.getPercentage());

        assertEquals(1, dashboardResult.getDonorAnalysis().getDonorTypeAnalysis().getNewDonorsCount());
        assertEquals(1, dashboardResult.getDonorAnalysis().getDonorTypeAnalysis().getRecurrentDonorsCount());
        assertEquals("donante1@email.com", dashboardResult.getDonorAnalysis().getTopDonors().get(0).getDonorEmail());
    }
}