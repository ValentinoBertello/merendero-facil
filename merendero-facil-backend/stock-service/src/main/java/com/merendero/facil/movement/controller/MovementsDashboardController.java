package com.merendero.facil.movement.controller;

import com.merendero.facil.movement.dto.report.MovementsDashboardDto;
import com.merendero.facil.movement.service.report.MovementsDashboardService;
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
public class MovementsDashboardController {

    private final MovementsDashboardService movementsDashboardService;

    /**
     * GET /dashboard/movements/**
     * Obtiene el resumen de movimientos (entradas y salidas) para un insumo
     * en un rango de fechas, agrupado por "day", "week" o "month".
     **/
    @GetMapping("/movements/{merenderoId}/{supplyId}/{startDate}/{endDate}/group/{groupBy}")
    @PreAuthorize("@merenderoSecurity.isOwner(#merenderoId, authentication.name)")
    public ResponseEntity<MovementsDashboardDto> getSummaryMovements(@PathVariable Long merenderoId,
                                                                     @PathVariable Long supplyId,
                                                                     @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                     @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                                     @PathVariable String groupBy) {
        return ResponseEntity.ok(this.movementsDashboardService.getSummaryMovements(
                supplyId, startDate, endDate, groupBy));
    }
}