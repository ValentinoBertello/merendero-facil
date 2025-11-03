package com.merendero.facil.service.impl;

import com.merendero.facil.clients.UserRestTemplate;
import com.merendero.facil.dto.apiExterna.UserResponse;
import com.merendero.facil.dto.merendero.MerenderoResponseDto;
import com.merendero.facil.entities.MerenderoEntity;
import com.merendero.facil.mapper.MerenderoDataMapper;
import com.merendero.facil.repository.MerenderoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.merendero.facil.helper.TestMerenderoHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MerenderoServiceImplTest {
    @Mock
    private MerenderoRepository merenderoRepository;
    @Mock
    private UserRestTemplate userRestTemplate;
    @Spy
    private MerenderoDataMapper merenderoDataMapper;

    @InjectMocks
    private MerenderoServiceImpl merenderoService;

    @Test
    void getAllMerenderos() {
        when(merenderoRepository.findByActiveTrue())
                .thenReturn(MERENDEROS);
        List<MerenderoResponseDto> result = this.merenderoService.getAllMerenderos();

        assertNotNull(result);
        assertEquals(5, result.size());
    }

    @Test
    void getMerenderoById() {
        when(merenderoRepository.findById(1L))
                .thenReturn(Optional.ofNullable(MERENDEROS.get(0)));
        MerenderoResponseDto result = this.merenderoService.getMerenderoById(1L);
        assertEquals("Tokio 170, X5152 Villa Carlos Paz, Córdoba", result.getAddress());
    }

    @Test
    void getMerenderoById_exception() {
        Long idInexistente = 999L;
        when(merenderoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            merenderoService.getMerenderoById(idInexistente);
        });

        assertEquals("Merendero con id 999 no encontrado", exception.getMessage());
    }

    @Test
    void getMerenderoByManagerEmail() {
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .build();
        when(userRestTemplate.getUserByEmail("valen@gmail.com")).thenReturn(userResponse);
        when(merenderoRepository.findByManagerId(1L)).thenReturn(Optional.ofNullable(MERENDERO_1));

        MerenderoResponseDto result = this.merenderoService.getMerenderoByManagerEmail("valen@gmail.com");

        assertEquals("Tokio 170, X5152 Villa Carlos Paz, Córdoba", result.getAddress());
    }

    @Test
    void getMerenderoByManagerEmail_exception() {
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .build();
        when(userRestTemplate.getUserByEmail("valen@gmail.com")).thenReturn(userResponse);
        when(merenderoRepository.findByManagerId(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            merenderoService.getMerenderoByManagerEmail("valen@gmail.com");
        });

        assertEquals("Merendero no encontrado", exception.getMessage());
    }

    @Test
    void createMerenderoWithAccessToken() {
        when(this.userRestTemplate.checkEmailExists("valen@gmail.com")).thenReturn(true);
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .build();
        when(userRestTemplate.getUserByEmail("valen@gmail.com")).thenReturn(userResponse);
        when(this.userRestTemplate.setManagerRole(1L)).thenReturn(userResponse);
        when(this.merenderoRepository.save(any())).thenReturn(MERENDERO_1);

        MerenderoResponseDto result =
                this.merenderoService.createMerenderoWithAccessToken(MERENDERO_REQUEST_1,"acces_token_1234");

        assertNotNull(result);
        assertEquals(MERENDERO_1.getId(), result.getId());
        assertEquals("acces_token_1234", result.getAccessToken());

        verify(userRestTemplate).checkEmailExists("valen@gmail.com");
        verify(userRestTemplate).getUserByEmail("valen@gmail.com");
        verify(userRestTemplate).setManagerRole(1L);
        verify(merenderoRepository).save(any(MerenderoEntity.class));
    }

    @Test
    void deleteMerenderoById_success() {
        when(merenderoRepository.findById(1L)).thenReturn(Optional.of(MERENDERO_1));

        MerenderoResponseDto result = merenderoService.deleteMerenderoById(1L);

        assertNotNull(result);
        assertEquals(MERENDERO_1.getId(), result.getId());
        verify(merenderoRepository).deleteById(1L);
    }

    @Test
    void deleteMerenderoById_notFound() {
        when(merenderoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> merenderoService.deleteMerenderoById(99L));

        assertEquals("Merendero no encontrado", exception.getMessage());
        verify(merenderoRepository, never()).deleteById(anyLong());
    }

    @Test
    void getMerenderosByCloseUbication_first() {
        when(merenderoRepository.findByActiveTrue())
                .thenReturn(MERENDEROS);

        BigDecimal originLat = new BigDecimal("-31.407185823566323");
        BigDecimal originLon = new BigDecimal("-64.47335160178234");
        int radiusKm = 40;

        List<MerenderoResponseDto> result = merenderoService.getMerenderosByCloseUbication(radiusKm, originLat, originLon);

        assertEquals("Tokio 170, X5152 Villa Carlos Paz, Córdoba", result.get(0).getAddress());
        assertEquals("Zuviria 280, X5152 Villa Carlos Paz, Córdoba", result.get(1).getAddress());
        assertEquals(5, result.size());
    }

    @Test
    void getMerenderosByCloseUbication_second() {
        when(merenderoRepository.findByActiveTrue())
                .thenReturn(MERENDEROS);

        BigDecimal originLat = new BigDecimal("-31.41270265399606");
        BigDecimal originLon = new BigDecimal("-64.48045791601841");
        int radiusKm = 40;

        List<MerenderoResponseDto> result = merenderoService.getMerenderosByCloseUbication(radiusKm, originLat, originLon);

        assertEquals("Fleming 241, X5152 Villa Carlos Paz, Córdoba", result.get(0).getAddress());
        assertEquals("Villalobos 270, X5152 Villa Carlos Paz, Córdoba", result.get(1).getAddress());
        assertEquals(5, result.size());
    }
}