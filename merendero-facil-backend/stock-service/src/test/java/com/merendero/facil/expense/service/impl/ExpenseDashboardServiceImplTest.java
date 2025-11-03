package com.merendero.facil.expense.service.impl;

import com.merendero.facil.common.clients.MerenderoRestTemplate;
import com.merendero.facil.expense.dto.statistics.ChartsData;
import com.merendero.facil.expense.dto.statistics.ExpenseDashboardResponse;
import com.merendero.facil.expense.dto.statistics.ExpenseSummaryData;
import com.merendero.facil.expense.dto.statistics.TimeGroupedExpenseData;
import com.merendero.facil.expense.repository.ExpenseRepository;
import com.merendero.facil.expense.service.DataGroupingService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.merendero.facil.helper.ExpenseTestHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

class ExpenseDashboardServiceImplTest {
    @Mock
    ExpenseRepository expenseRepository;
    @Mock
    private MerenderoRestTemplate merenderoRestTemplate;
    @Mock
    DataGroupingService dataGroupingService;
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    ExpenseDashboardServiceImpl expenseDashboardService;

    @Test
    void getExpenseDashboard() {
        String authToken = "Bearer fake-token-123";
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);

        when(expenseRepository.findByDatesAndMerendero(
                start.atStartOfDay(),
                end.atTime(LocalTime.MAX),
                1L)).thenReturn(ALL_EXPENSES);

        when(request.getHeader("Authorization")).thenReturn(authToken);
        when(merenderoRestTemplate.getDonationsByMerenderoAndDates(
                1L,
                start,
                end,
                "day",
                authToken)).thenReturn(ALL_DONATIONS);

        when(dataGroupingService.groupDataByPeriod(
                any(),
                any(),
                any())).thenReturn(List.of(new TimeGroupedExpenseData()));

        ExpenseDashboardResponse result =
                this.expenseDashboardService.getExpenseDashboard(1L, start, end, "day", request);

        assertNotNull(result);

        ExpenseSummaryData summaryData = result.getExpenseSummaryData();
        assertEquals(new BigDecimal("15301.25"), summaryData.getTotalExpenseAmount());
        assertEquals(new BigDecimal("20500.00"), summaryData.getTotalDonationAmount());
        assertEquals(new BigDecimal("5198.75"), summaryData.getTotalProfitOrLoss());
        assertEquals(new BigDecimal("167.70"), summaryData.getDailyProfitOrLoss());
        assertEquals(new BigDecimal("34.00"), summaryData.getProfitOrLossPercentage());

        ChartsData chartsData = result.getChartsData();

        assertEquals(3, chartsData.getExpensesByType().size());
        assertEquals(new BigDecimal("13000.50"), chartsData.getExpensesByType().get(0).getAmount());
        assertEquals("Compra de Insumos", chartsData.getExpensesByType().get(0).getExpenseType());

        assertEquals(3, chartsData.getExpensesBySupply().size());
        assertEquals(new BigDecimal("7500.00"), chartsData.getExpensesBySupply().get(0).getAmount());
        assertEquals("Arroz", chartsData.getExpensesBySupply().get(0).getSupplyName());
    }
}