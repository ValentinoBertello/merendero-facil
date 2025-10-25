package com.merendero.facil.movement.controller;

import com.merendero.facil.movement.dto.purchase.PurchaseRequestDto;
import com.merendero.facil.movement.dto.purchase.PurchaseResponseDto;
import com.merendero.facil.movement.service.entry.SupplyPurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = "*")
@RestController
@RequestMapping("/supplies-purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final SupplyPurchaseService purchaseService;

    /**
     * POST /supplies-purchases
     * Registra una nueva compra de insumos, sería la inserción al mismo tiempo
     * de un Entrada de insumos y un gasto. Ambos asociados.
     **/
    @PostMapping
    @PreAuthorize("@merenderoSecurity.isOwner(#requestDto.expenseRequestDto.merenderoId, authentication.name)")
    public ResponseEntity<PurchaseResponseDto> saveEntryAndExpense(@RequestBody PurchaseRequestDto requestDto) {
        return ResponseEntity.ok(this.purchaseService.createPurchase(requestDto));
    }
}
