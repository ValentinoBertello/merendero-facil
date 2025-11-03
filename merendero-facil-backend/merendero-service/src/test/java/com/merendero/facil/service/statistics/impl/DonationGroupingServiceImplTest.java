package com.merendero.facil.service.statistics.impl;

import com.merendero.facil.dto.donation.statistics.DonationDateSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.merendero.facil.helper.TestDonationHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class DonationGroupingServiceImplTest {

    @InjectMocks
    private DonationGroupingServiceImpl donationGroupingService;

    @Test
    void groupDonationsByPeriodDay() {
        List<DonationDateSummary> result = this.donationGroupingService.groupDonationsByPeriod(ALL_DONATIONS,
                "day");

        assertEquals(8, result.size());
        assertEquals(new BigDecimal("4300.00"), result.get(2).getAmountDonated());
    }

    @Test
    void groupDonationsByPeriodWeek() {
        List<DonationDateSummary> result = this.donationGroupingService.groupDonationsByPeriod(ALL_DONATIONS,
                "week");

        assertEquals(5, result.size());
        assertEquals(new BigDecimal("2000.00"), result.get(0).getAmountDonated());
        assertEquals(LocalDate.of(2024, 1, 8), result.get(0).getDate());

        assertEquals(new BigDecimal("6800.00"), result.get(1).getAmountDonated());
        assertEquals(LocalDate.of(2024, 1, 15), result.get(1).getDate());
    }

    @Test
    void groupDonationsByPeriodMonth() {
        List<DonationDateSummary> result = this.donationGroupingService.groupDonationsByPeriod(ALL_DONATIONS,
                "month");

        assertEquals(3, result.size());
        assertEquals(new BigDecimal("8800.00"), result.get(0).getAmountDonated());
    }
}