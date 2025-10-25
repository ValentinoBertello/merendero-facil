package com.merendero.facil.movement.controller;

import com.merendero.facil.movement.dto.entry.EntryRequestDto;
import com.merendero.facil.movement.dto.entry.EntryResponseDto;
import com.merendero.facil.movement.service.entry.EntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(originPatterns = "*")
@RestController
@RequestMapping("/entries")
@RequiredArgsConstructor
public class EntryController {

    private final EntryService entryService;

    /**
     * POST /entries
     * Registra un nueva entrada de insumos desde el front.
     * A un merendero en específico.
     */
    @PostMapping
    @PreAuthorize("@merenderoSecurity.isOwner(#requestDto.merenderoId, authentication.name)")
    public ResponseEntity<EntryResponseDto> saveEntry(@RequestBody EntryRequestDto requestDto) {
        return ResponseEntity.ok(this.entryService.createEntry(requestDto));
    }

    /**
     * GET /entries/{merenderoId}
     * Trae todas las entradas de insumos de un merendero en específico.
     */
    @GetMapping("/{merenderoId}")
    @PreAuthorize("@merenderoSecurity.isOwner(#merenderoId, authentication.name)")
    public ResponseEntity<List<EntryResponseDto>> getEntriesFromMerendero(@PathVariable Long merenderoId) {
        return ResponseEntity.ok(this.entryService.getEntriesFromMerendero(merenderoId));
    }
}