package com.merendero.facil.common.security;

import com.merendero.facil.common.clients.MerenderoRestTemplate;
import com.merendero.facil.common.clients.dto.MerenderoResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

/**
 * Bean de seguridad que verifica mediante el microservicio, si el usuario autenticado
 * es el dueño del merendero pasado por parámetro.
 **/
@Component("merenderoSecurity")
public class MerenderoSecurity {

    private final MerenderoRestTemplate merenderoRestTemplate;

    public MerenderoSecurity(MerenderoRestTemplate merenderoRestTemplate) {
        this.merenderoRestTemplate = merenderoRestTemplate;
    }

    /**
     * Verifica si el usuario (username) es el manager del merendero.
     * Devuelve false si no coincide o si hay algún error.
     **/
    public boolean isOwner(Long merenderoId, String username) {
        try {
            MerenderoResponse merendero = merenderoRestTemplate.getMerenderoById(merenderoId);
            return username != null && username.equals(merendero.getManagerEmail());
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (RestClientException e) {
            return false;
        }
    }
}
