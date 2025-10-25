package com.merendero.facil.supply.controller;

import com.merendero.facil.supply.dto.SupplyRequestDto;
import com.merendero.facil.supply.dto.SupplyResponseDto;
import com.merendero.facil.supply.entity.SupplyCategoryEntity;
import com.merendero.facil.supply.service.SupplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(originPatterns = "*")
@RestController
@RequestMapping("/supplies")
@RequiredArgsConstructor
public class SupplyController {

    private final SupplyService supplyService;

    /**
     * POST /supplies/{merenderoId}
     * Crea y guarda un nuevo insumo asociado a un merendero específico.
     **/
    @PostMapping("/{merenderoId}")
    @PreAuthorize("@merenderoSecurity.isOwner(#merenderoId, authentication.name)")
    public ResponseEntity<SupplyResponseDto> saveSupply(@PathVariable Long merenderoId,
                                                        @RequestBody SupplyRequestDto supplyRequestDto) {
        return ResponseEntity.ok(this.supplyService.saveSupply(merenderoId, supplyRequestDto));
    }

    /**
     * GET /supplies/{merenderoId}
     * Trae todos los insumos activos asociados a un merendero específico.
     **/
    @GetMapping("/{merenderoId}")
    @PreAuthorize("@merenderoSecurity.isOwner(#merenderoId, authentication.name)")
    public ResponseEntity<List<SupplyResponseDto>> getSuppliesFromMerendero(@PathVariable Long merenderoId) {
        return ResponseEntity.ok(this.supplyService.getSuppliesFromMerendero(merenderoId));
    }

    /**
     * GET /supplies/categories
     * Trae todas las categorías de insumos.
     **/
    @GetMapping("/categories")
    public ResponseEntity<List<SupplyCategoryEntity>> getSupplyCategories() {
        return ResponseEntity.ok(this.supplyService.getSupplyCategories());
    }

    /**
     * DELETE /supplies/{merenderoId}/{supplyId}
     * Elimina un insumo de un merendero específico (marcándolo como inactivo).
     **/
    @DeleteMapping("/{merenderoId}/{supplyId}")
    @PreAuthorize("@merenderoSecurity.isOwner(#merenderoId, authentication.name)")
    public ResponseEntity<Long> removeSupplyFromMerendero(@PathVariable Long merenderoId, @PathVariable Long supplyId) {
        return ResponseEntity.ok(this.supplyService.removeSupplyFromMerendero(merenderoId, supplyId));
    }
}