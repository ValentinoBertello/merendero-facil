package com.merendero.facil.movement.controller;

import com.merendero.facil.movement.dto.output.OutputRequestDto;
import com.merendero.facil.movement.dto.output.OutputResponseDto;
import com.merendero.facil.movement.service.output.OutputService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(originPatterns = "*")
@RestController
@RequestMapping("/outputs")
@RequiredArgsConstructor
public class OutputController {

    private final OutputService outputService;

    /**
     * POST /outputs
     * Registra un nueva salida de insumos desde el front.
     * A un merendero en específico.
     **/
    @PostMapping
    @PreAuthorize("@merenderoSecurity.isOwner(#requestDto.merenderoId, authentication.name)")
    public ResponseEntity<OutputResponseDto> saveOutput(@RequestBody OutputRequestDto requestDto) {
        return ResponseEntity.ok(this.outputService.createOutput(requestDto));
    }

    /**
     * GET /outputs/{merenderoId}
     * Trae todas las salidas de insumos de un merendero en específico.
     **/
    @GetMapping("/{merenderoId}")
    @PreAuthorize("@merenderoSecurity.isOwner(#merenderoId, authentication.name)")
    public ResponseEntity<List<OutputResponseDto>> getOutputsFromMerendero(@PathVariable Long merenderoId) {
        return ResponseEntity.ok(this.outputService.getOutputsFromMerendero(merenderoId));
    }
}
