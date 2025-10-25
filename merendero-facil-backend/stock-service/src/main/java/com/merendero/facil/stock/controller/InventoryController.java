package com.merendero.facil.stock.controller;

import com.merendero.facil.stock.dto.ItemStockDto;
import com.merendero.facil.stock.dto.LotDto;
import com.merendero.facil.stock.service.LotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@CrossOrigin(originPatterns = "*")
@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final LotService lotService;

    /**
     * GET /inventory/{merenderoId}
     * Trae el stock completo de un merendero.
     * Cada ItemStockDto contiene un insumo y su stock actual, entre otras cosas.
     **/
    @GetMapping("/{merenderoId}")
    @PreAuthorize("@merenderoSecurity.isOwner(#merenderoId, authentication.name)")
    public ResponseEntity<List<ItemStockDto>> getStockItemsByMerendero(@PathVariable Long merenderoId) {
        return ResponseEntity.ok(this.lotService.getStockItems(merenderoId));
    }

    /**
     * GET /inventory/lots/{merenderoId}/{supplyId}
     * Devuelve los lotes de un insumo específico.
     **/
    @GetMapping("/lots/{merenderoId}/{supplyId}")
    @PreAuthorize("@merenderoSecurity.isOwner(#merenderoId, authentication.name)")
    public ResponseEntity<List<LotDto>> getLotsBySupply(@PathVariable Long merenderoId,
                                                        @PathVariable Long supplyId) {
        return ResponseEntity.ok(this.lotService.getLotsBySupply(merenderoId, supplyId));
    }

    /**
     * GET /inventory/supply/stock/{merenderoId}/{supplyId}
     * Devuelve el stock total de un insumo en particular
     **/
    @GetMapping("/supply/stock/{merenderoId}/{supplyId}")
    @PreAuthorize("@merenderoSecurity.isOwner(#merenderoId, authentication.name)")
    public ResponseEntity<BigDecimal> getTotalStockBySupply(@PathVariable Long merenderoId,
                                                            @PathVariable Long supplyId) {
        return ResponseEntity.ok(this.lotService.getTotalStockBySupply(merenderoId, supplyId));
    }

    /**
     * GET /inventory/check-stock/{supplyId}/{quantity}/{merenderoId}
     * Verifica si hay stock suficiente para la cantidad de un insumo específico
     */
    @GetMapping("/check-stock/{supplyId}/{quantity}/{merenderoId}")
    @PreAuthorize("@merenderoSecurity.isOwner(#merenderoId, authentication.name)")
    public ResponseEntity<Boolean> checkSupplyStock(@PathVariable Long merenderoId,
                                                    @PathVariable Long supplyId,
                                                    @PathVariable BigDecimal quantity){
        return ResponseEntity.ok(this.lotService.checkSupplyStock(merenderoId, supplyId, quantity));
    }
}