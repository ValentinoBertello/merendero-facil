package com.merendero.facil.controllers;

import com.merendero.facil.dto.merendero.MerenderoResponseDto;
import com.merendero.facil.service.MerenderoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@CrossOrigin(originPatterns = "*")
@RestController
@RequestMapping("/merenderos")
public class MerenderoController {

    private final MerenderoService merenderoService;

    public MerenderoController(MerenderoService merenderoService) {
        this.merenderoService = merenderoService;
    }

    /**
     * GET /merenderos
     * Devuelve la lista completa de merenderos disponibles.
     */
    @GetMapping
    public ResponseEntity<List<MerenderoResponseDto>> getAllMerenderos(){
        return ResponseEntity.ok(this.merenderoService.getAllMerenderos());
    }

    /**
     * GET /merenderos/byManager/{managerEmail}
     * Busca y devuelve el merendero asociado al email del encargado de dicho centro.
     */
    @GetMapping("/byManager/{managerEmail}")
    public ResponseEntity<MerenderoResponseDto> getMerenderoByManagerEmail(@PathVariable String managerEmail){
        return ResponseEntity.ok(this.merenderoService.getMerenderoByManagerEmail(managerEmail));
    }

    /**
     * GET /merenderos/byId/{id}
     * Busca y devuelve un merendero por su identificador.
     */
    @GetMapping("/byId/{id}")
    public ResponseEntity<MerenderoResponseDto> getMerenderoById(@PathVariable Long id){
        if(this.merenderoService.getMerenderoById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(this.merenderoService.getMerenderoById(id));
    }

    /**
     * GET /merenderos/close/{size}/{originLatitude}/{originLongitude}
     * Devuelve los merenderos más cercanos a una ubicación dada.
     * - km: radio de resultados a devolver.
     * - originLatitude / originLongitude: coordenadas desde las cuales medir la cercanía.
     */
    @GetMapping("/close/{km}/{originLatitude}/{originLongitude}")
    public ResponseEntity<List<MerenderoResponseDto>> getMerenderosByCloseUbication(@PathVariable Integer km,
                                                                                    @PathVariable BigDecimal originLatitude,
                                                                                    @PathVariable BigDecimal originLongitude){
        return ResponseEntity.ok
                (this.merenderoService.getMerenderosByCloseUbication(km, originLatitude, originLongitude));
    }

    /**
     * DELETE /merenderos/delete/{merenderoId}
     * Elimina un merendero por su ID.
     */
    @DeleteMapping("/delete/{merenderoId}")
    public ResponseEntity<MerenderoResponseDto> deleteMerenderoById(@PathVariable Long merenderoId){
        return ResponseEntity.ok(this.merenderoService.deleteMerenderoById(merenderoId));
    }
}