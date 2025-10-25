package com.merendero.facil.security;

import com.merendero.facil.entities.MerenderoEntity;
import com.merendero.facil.repository.MerenderoRepository;
import org.springframework.stereotype.Component;

/**
 * Bean de seguridad que verifica si el usuario autenticado es dueño del merendero pasado
 * por parámetro.
 **/
@Component("merenderoSecurity")
public class MerenderoSecurity {

    private final MerenderoRepository merenderoRepository;

    public MerenderoSecurity(MerenderoRepository merenderoRepository) {
        this.merenderoRepository = merenderoRepository;
    }

    /**
     * Verifica si el usuario autenticado es el manager del merendero.
     * Devuelve false si no coincide o si hay algun error.
     **/
    public boolean isOwner(Long merenderoId, String username) {
        MerenderoEntity merenderoEntity = this.merenderoRepository.findById(merenderoId).
        orElseThrow(() -> new RuntimeException("Merendero no encontrado"));
        return username != null && username.equals(merenderoEntity.getManagerEmail());
    }
}