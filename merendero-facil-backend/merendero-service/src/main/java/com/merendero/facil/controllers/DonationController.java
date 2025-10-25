package com.merendero.facil.controllers;

import com.merendero.facil.dto.donation.DonationResponseDto;
import com.merendero.facil.service.DonationService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(originPatterns = "*")
@RestController
@RequestMapping("/donations")
public class DonationController {
    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    /**
     * GET /donations
     * Obtiene todas las donaciones asociadas a un merendero específico.
     */
    @GetMapping("/{merenderoId}")
    @PreAuthorize("@merenderoSecurity.isOwner(#merenderoId, authentication.name)")
    public ResponseEntity<List<DonationResponseDto>> getDonationByMerenderoId(@PathVariable Long merenderoId) {
        return ResponseEntity.ok(this.donationService.getDonationByMerenderoId(merenderoId));
    }

    /**
     * GET /donations/search
     * Obtiene donaciones filtradas por merendero, fechas y email del donante con soporte de paginación.
     */
    @GetMapping("/search")
    @PreAuthorize("@merenderoSecurity.isOwner(#merenderoId, authentication.name)")
    public ResponseEntity<?> getDonationPagesByFilters(
            @RequestParam Long merenderoId,
            @RequestParam(required = false) LocalDate sinceDate,
            @RequestParam(required = false) LocalDate untilDate,
            @RequestParam(required = false) String donorEmail,
            Pageable pageable) {
        return ResponseEntity.ok(this.donationService.getDonationPagesByFilters(
                merenderoId, sinceDate, untilDate, donorEmail, pageable));
    }
}
