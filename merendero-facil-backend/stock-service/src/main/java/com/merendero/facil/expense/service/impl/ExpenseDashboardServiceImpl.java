package com.merendero.facil.expense.service.impl;

import com.merendero.facil.common.clients.MerenderoRestTemplate;
import com.merendero.facil.common.clients.dto.DonationDateSummary;
import com.merendero.facil.expense.dto.statistics.*;
import com.merendero.facil.expense.entity.ExpenseEntity;
import com.merendero.facil.expense.repository.ExpenseRepository;
import com.merendero.facil.expense.service.DataGroupingService;
import com.merendero.facil.expense.service.ExpenseDashboardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpenseDashboardServiceImpl implements ExpenseDashboardService {

    private final ExpenseRepository expenseRepository;
    private final MerenderoRestTemplate merenderoRestTemplate;
    private final DataGroupingService dataGroupingService;

    /**
     * Método principal que orquesta la generación del dashboard de gastos
     *
     * Obtiene y combina datos de gastos y donaciones del microservicio
     * para construir el reporte financiero completo con gráficos y métricas
     */
    @Override
    public ExpenseDashboardResponse getExpenseDashboard(Long merenderoId, LocalDate startDate, LocalDate endDate,
                                                        String groupBy, HttpServletRequest request) {
        // Obtiene todos los gastos entre las fechas recibidas
        List<ExpenseEntity> expenses = this.getExpensesInDateRange(startDate, endDate, merenderoId);
        if (expenses.isEmpty()) {
            return new ExpenseDashboardResponse();
        }

        // Obtiene todas las donaciones entre las fechas recibidas, llamando al microservicio
        List<DonationDateSummary> donations =
                this.merenderoRestTemplate.getDonationsByMerenderoAndDates(merenderoId, startDate, endDate, groupBy,
                        request.getHeader("Authorization"));
        log.info("donations: {}", donations);

        ChartsData chartsData = this.buildChartsData(expenses, donations, groupBy);
        ExpenseSummaryData summaryData = this.buildExpenseSummaryData(expenses, donations, startDate, endDate);

        return ExpenseDashboardResponse.builder()
                .expenseSummaryData(summaryData)
                .chartsData(chartsData)
                .build();
    }

    /**
     * Construye el resumen financiero del dashboard con totales, promedios y porcentajes
     */
    private ExpenseSummaryData buildExpenseSummaryData(List<ExpenseEntity> expenses, List<DonationDateSummary> donations
            , LocalDate startDate, LocalDate endDate) {
        BigDecimal totalExpenseAmount = this.calculateTotalExpenseAmount(expenses);
        BigDecimal totalDonationAmount = this.calculateTotalDonationAmount(donations);
        BigDecimal totalProfitOrLoss = totalDonationAmount.subtract(totalExpenseAmount);

        return ExpenseSummaryData.builder()
                .totalExpenseAmount(totalExpenseAmount)
                .totalDonationAmount(totalDonationAmount)
                .totalProfitOrLoss(totalProfitOrLoss)
                .dailyProfitOrLoss(this.calculateDailyProfitOrLoss(totalProfitOrLoss, startDate, endDate))
                .profitOrLossPercentage(this.calculateProfitLossPercentage(totalProfitOrLoss, totalExpenseAmount))
                .build();
    }

    /**
     * Construye todos los datos necesarios para los gráficos del dashboard
     */
    private ChartsData buildChartsData(List<ExpenseEntity> expenses, List<DonationDateSummary> donations, String groupBy) {
        return ChartsData.builder()
                .timeGroupedData(this.dataGroupingService.groupDataByPeriod(expenses, donations, groupBy))
                .expensesByType(this.getExpensesByType(expenses))
                .expensesBySupply(this.getExpensesBySupply(expenses))
                .build();
    }

    /**
     * Calcula el porcentaje de ganancia/pérdida en relación a los gastos totales
     */
    private BigDecimal calculateProfitLossPercentage(BigDecimal totalProfitOrLoss, BigDecimal totalExpenseAmount) {
        return totalProfitOrLoss
                .divide(totalExpenseAmount, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula el promedio diario de ganancia/pérdida en el período
     */
    private BigDecimal calculateDailyProfitOrLoss(BigDecimal totalProfitOrLoss, LocalDate startDate, LocalDate endDate) {
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return totalProfitOrLoss.divide(BigDecimal.valueOf(daysBetween), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula la suma total de todas las donaciones recibidas
     */
    private BigDecimal calculateTotalDonationAmount(List<DonationDateSummary> donations) {
        return donations.stream()
                .map(DonationDateSummary::getAmountDonated)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula la suma total de todos los gastos realizados
     */
    private BigDecimal calculateTotalExpenseAmount(List<ExpenseEntity> expenses) {
        return expenses.stream()
                .map(ExpenseEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Obtiene los gastos de insumos y agrupados por el insumo comprado
     */
    private List<ExpenseBySupplyDto> getExpensesBySupply(List<ExpenseEntity> expenses) {
        List<ExpenseEntity> supplyExpenses = expenses.stream().
                filter(ex -> "Compra de Insumos".equals(ex.getType().getDescription()))
                .toList();

        return supplyExpenses.stream()
                .collect(Collectors.groupingBy(
                        expense -> expense.getEntry().getSupply().getName(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                ExpenseEntity::getAmount,
                                BigDecimal::add
                        )
                )).entrySet().stream().map(
                        entry -> ExpenseBySupplyDto.builder()
                                .supplyName(entry.getKey())
                                .amount(entry.getValue())
                                .build())
                .sorted(Comparator.comparing(ExpenseBySupplyDto::getAmount).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Obtiene los gastos agrupados por tipo con sus montos totales
     */
    private List<ExpenseByTypeDto> getExpensesByType(List<ExpenseEntity> expenses) {
        return expenses.stream()
                .collect(Collectors.groupingBy(
                        expense -> expense.getType().getDescription(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                ExpenseEntity::getAmount,
                                BigDecimal::add
                        )
                )).entrySet().stream()
                .map(entry -> ExpenseByTypeDto.builder()
                        .expenseType(entry.getKey())
                        .amount(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(ExpenseByTypeDto::getAmount).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Obtiene los gastos de un merendero en un rango de fechas (convertido a LocalDateTime automáticamente)
     */
    private List<ExpenseEntity> getExpensesInDateRange(LocalDate startDate, LocalDate endDate, Long merenderoId) {
        List<ExpenseEntity> expenses = this.expenseRepository.findByDatesAndMerendero(
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX),
                merenderoId);
        log.info("expenseEntities: {}", expenses);
        return expenses;
    }
}
