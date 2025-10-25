package com.merendero.facil.controllers;

import com.merendero.facil.dto.donation.statistics.DonationDashboardResponse;
import com.merendero.facil.dto.donation.statistics.DonationDateSummary;
import com.merendero.facil.service.statistics.DonationStatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(originPatterns = "*")
@RestController
@RequestMapping("/reports")
public class DonationReportController {

    private final DonationStatisticsService donationStatisticsService;

    public DonationReportController(DonationStatisticsService donationStatisticsService) {
        this.donationStatisticsService = donationStatisticsService;
    }

    /**
     * GET /reports/{merenderoId}/{startDate}/{endDate}/group/{groupBy}
     * Obtiene el dashboard de donaciones para un merendero en un rango de fechas
     **/
    @GetMapping("/{merenderoId}/{startDate}/{endDate}/group/{groupBy}")
    @PreAuthorize("@merenderoSecurity.isOwner(#merenderoId, authentication.name)")
    public ResponseEntity<DonationDashboardResponse> getDonationDashboard(
            @PathVariable Long merenderoId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            @PathVariable String groupBy) {
        return ResponseEntity.ok(this.donationStatisticsService.getDonationDashboard(startDate, endDate, groupBy,
                merenderoId));
    }

    /**
     * GET /reports/grouped-summary/{merenderoId}/{startDate}/{endDate}/group/{groupBy}
     * Obtiene un resumen agrupado de donaciones por fecha
     **/
    @GetMapping("grouped-summary/{merenderoId}/{startDate}/{endDate}/group/{groupBy}")
    @PreAuthorize("@merenderoSecurity.isOwner(#merenderoId, authentication.name)")
    public ResponseEntity<List<DonationDateSummary>> getDonationsGroupedSummary(
            @PathVariable Long merenderoId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PathVariable String groupBy) {
        return ResponseEntity.ok(donationStatisticsService.getDonationGroupsByTime(merenderoId, startDate, endDate,
                groupBy));
    }
}