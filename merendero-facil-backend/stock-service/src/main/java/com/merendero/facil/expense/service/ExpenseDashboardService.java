package com.merendero.facil.expense.service;

import com.merendero.facil.expense.dto.statistics.ExpenseDashboardResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;

public interface ExpenseDashboardService {
    ExpenseDashboardResponse getExpenseDashboard(Long merenderoId, LocalDate startDate, LocalDate endDate,
                                                 String groupBy, HttpServletRequest request);
}
