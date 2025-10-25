package com.merendero.facil.service.impl;

import com.merendero.facil.clients.UserRestTemplate;
import com.merendero.facil.dto.merendero.MerenderoRequestDto;
import com.merendero.facil.dto.merendero.MerenderoResponseDto;
import com.merendero.facil.entities.MerenderoEntity;
import com.merendero.facil.mapper.MerenderoDataMapper;
import com.merendero.facil.repository.MerenderoRepository;
import com.merendero.facil.service.MerenderoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Toda la lógica de negocio referida a los Merenderos.
 * **/
@Service
public class MerenderoServiceImpl implements MerenderoService {

    private final MerenderoRepository merenderoRepository;
    private final UserRestTemplate userRestTemplate;
    private final MerenderoDataMapper merenderoDataMapper;

    public MerenderoServiceImpl(MerenderoRepository merenderoRepository, UserRestTemplate userRestTemplate, MerenderoDataMapper merenderoDataMapper) {
        this.merenderoRepository = merenderoRepository;
        this.userRestTemplate = userRestTemplate;
        this.merenderoDataMapper = merenderoDataMapper;
    }

    /**
     * Recupera todos los merenderos activos.
     */
    @Override
    @Transactional(readOnly = true)
    public List<MerenderoResponseDto> getAllMerenderos() {
        List<MerenderoEntity> merenderoEntities = this.merenderoRepository.findByActiveTrue();
        return this.merenderoDataMapper.mapMerenderoEntitiesToMerenderoResponses(merenderoEntities);
    }

    /**
     * Busca un merendero por su ID.
     */
    @Override
    @Transactional(readOnly = true)
    public MerenderoResponseDto getMerenderoById(Long id) {
        MerenderoEntity merenderoEntity = this.merenderoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Merendero con id " + id + " no encontrado"));
        return this.merenderoDataMapper.mapMerenderoEntityToMerenderoResponse(merenderoEntity);
    }

    /**
     * Obtiene el merendero gestionado por el usuario cuyo email se pasa como parámetro.
     */
    @Override
    public MerenderoResponseDto getMerenderoByManagerEmail(String managerEmail) {
        Long managerId = this.userRestTemplate.getUserByEmail(managerEmail).getId();
        MerenderoEntity merenderoEntity = this.merenderoRepository.findByManagerId(managerId).
                orElseThrow(() -> new RuntimeException("Merendero no encontrado"));
        return this.merenderoDataMapper.mapMerenderoEntityToMerenderoResponse(merenderoEntity);
    }

    /**
     * Crea un nuevo merendero en la base de datos con su token de acceso de Mercado Pago.
     *
     * Este método se ejecuta después de que el usuario autoriza nuestra aplicación en Mercado Pago y
     * hemos canjeado exitosamente el código de autorización por un token de acceso permanente.
     */
    @Override
    public MerenderoResponseDto createMerenderoWithAccessToken(MerenderoRequestDto merenderoData, String accessToken) {
        this.validateManager(merenderoData.getManagerEmail());
        Long managerId = this.userRestTemplate.getUserByEmail(merenderoData.getManagerEmail()).getId();
        this.userRestTemplate.setManagerRole(managerId);
        MerenderoEntity merenderoEntity =
                this.merenderoDataMapper.mapMerenderoRequestToMerenderoEntity(merenderoData, managerId);
        merenderoEntity.setAccessToken(accessToken);
        merenderoEntity = this.merenderoRepository.save(merenderoEntity);
        return this.merenderoDataMapper.mapMerenderoEntityToMerenderoResponse(merenderoEntity);
    }

    /**
     * Elimina un merendero por su ID.
     */
    @Override
    @Transactional
    public MerenderoResponseDto deleteMerenderoById(Long merenderoId) {
        MerenderoEntity merenderoEntity = this.merenderoRepository.findById(merenderoId).
                orElseThrow(() -> new RuntimeException("Merendero no encontrado"));
        this.merenderoRepository.deleteById(merenderoId);
        return this.merenderoDataMapper.mapMerenderoEntityToMerenderoResponse(merenderoEntity);
    }

    /**
     * Devuelve una lista de merenderos cercanos a una ubicación dada.

     * - km: radio a traer (en kilometros)
     * - originLatitude / originLongitude: coordenadas de referencia.
     */
    @Override
    @Transactional(readOnly = true)
    public List<MerenderoResponseDto> getMerenderosByCloseUbication(Integer km,
                                                                    BigDecimal originLatitude,
                                                                    BigDecimal originLongitude) {
        int radiusKm = Math.abs(km);
        // Delegamos la lógica de búsqueda/filtrado/orden al método findNearbyMerenderos
        List<MerenderoEntity> nearbyMerenderos = findNearbyMerenderos(originLatitude, originLongitude, radiusKm);
        return merenderoDataMapper.mapMerenderoEntitiesToMerenderoResponses(nearbyMerenderos);
    }

    /**
     * Método privado que devuelve la lista de merenderos dentro del radio,
     * ordenada por distancia ascendente.
     */
    private List<MerenderoEntity> findNearbyMerenderos(BigDecimal originLat,
                                                       BigDecimal originLon,
                                                       int radiusKm) {
        List<MerenderoEntity> activeMerenderos = merenderoRepository.findByActiveTrue();
        return activeMerenderos.stream()
                 // Transformamos cada MerenderoEntity en un map (Entry).
                 // Donde la key es la entidad y el value es la distancia calculada desde el punto de origen (en km)
                .map(m -> new AbstractMap.SimpleEntry<>(
                        m,
                        calculateHaversineDistance(
                                originLat.doubleValue(),
                                originLon.doubleValue(),
                                m.getLatitude().doubleValue(),
                                m.getLongitude().doubleValue()
                        )
                ))
                 // Reducimos la colección a los merenderos dentro del radio
                .filter(entry -> entry.getValue() <= radiusKm)
                // Ordenamos por distancia.
                .sorted(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Valida que el manager exista consultando el micro de Users.
     */
    private void validateManager(String managerEmail) {
        Boolean aux = this.userRestTemplate.checkEmailExists(managerEmail);
        if(!aux) {
            throw new IllegalArgumentException("No existe ese usuario");
        }
    }

    /**
     * Calcula la distancia en kilómetros entre dos puntos geográficos usando la fórmula de Haversine.
     */
    private double calculateHaversineDistance(double originLat, double originLon, double merenderoLat, double merenderoLon) {
        final int R = 6371;
        double latDistance = Math.toRadians(merenderoLat - originLat);
        double lonDistance = Math.toRadians(merenderoLon - originLon);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(originLat)) * Math.cos(Math.toRadians(merenderoLat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
