package com.merendero.facil.expense.controller;

import com.merendero.facil.expense.dto.statistics.ExpenseDashboardResponse;
import com.merendero.facil.expense.service.ExpenseDashboardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@CrossOrigin(originPatterns = "*")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class ExpenseDashboardController {

    private final ExpenseDashboardService expenseDashboardService;

    /**
     * GET /dashboard/expenses/**
     * Obtiene el resumen de gastos completo de un merendero.
     **/
    @GetMapping("/expenses/{merenderoId}/{startDate}/{endDate}/group/{groupBy}")
    @PreAuthorize("@merenderoSecurity.isOwner(#merenderoId, authentication.name)")
    public ResponseEntity<ExpenseDashboardResponse> getExpenseDashboard(@PathVariable Long merenderoId,
                                                                        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                                        @PathVariable String groupBy,
                                                                        HttpServletRequest request) {
        return ResponseEntity.ok(this.expenseDashboardService.getExpenseDashboard(
                merenderoId, startDate, endDate, groupBy, request));
    }
}