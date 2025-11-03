package com.merendero.facil.expense.service.impl;

import com.merendero.facil.common.clients.dto.DonationDateSummary;
import com.merendero.facil.expense.dto.statistics.TimeGroupedExpenseData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static com.merendero.facil.helper.ExpenseTestHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class DataGroupingServiceImplTest {

    @InjectMocks
    DataGroupingServiceImpl dataGroupingService;

    @Test
    void groupDataByPeriodDay() {
        List<TimeGroupedExpenseData> result = this.dataGroupingService.groupDataByPeriod(
                                                                        ALL_EXPENSES,
                                                                        ALL_DONATIONS,
                                                                "day");
        assertEquals(5, result.size());
        assertEquals(new BigDecimal("800.75"), result.get(0).getExpenseAmount());
        assertEquals(new BigDecimal("0"), result.get(0).getDonationAmount());
        assertEquals(new BigDecimal("7500.00"), result.get(2).getExpenseAmount());
        assertEquals(new BigDecimal("12500.00"), result.get(2).getDonationAmount());
    }

    @Test
    void groupDataByPeriodWeek() {
        List<DonationDateSummary> donations = List.of(DONATION_WEEKLY, DONATION_WEEKLY_2);
        List<TimeGroupedExpenseData> result = this.dataGroupingService.groupDataByPeriod(
                ALL_EXPENSES,
                donations,
                "week");
        assertEquals(4, result.size());
        assertEquals(new BigDecimal("800.75"), result.get(0).getExpenseAmount());
        assertEquals(new BigDecimal("0"), result.get(0).getDonationAmount());
        assertEquals(new BigDecimal("5500.50"), result.get(3).getExpenseAmount());
        assertEquals(new BigDecimal("8000.00"), result.get(3).getDonationAmount());
    }

    @Test
    void groupDataByPeriodMonth() {
        List<DonationDateSummary> donations = List.of(DONATION_MONTHLY, DONATION_MONTHLY_2);
        List<TimeGroupedExpenseData> result = this.dataGroupingService.groupDataByPeriod(
                ALL_EXPENSES,
                donations,
                "month");
        assertEquals(2, result.size());
        assertEquals(new BigDecimal("15301.25"), result.get(0).getExpenseAmount());
        assertEquals(new BigDecimal("20500.00"), result.get(0).getDonationAmount());
        assertEquals(new BigDecimal("0"), result.get(1).getExpenseAmount());
        assertEquals(new BigDecimal("51200.00"), result.get(1).getDonationAmount());
    }
}