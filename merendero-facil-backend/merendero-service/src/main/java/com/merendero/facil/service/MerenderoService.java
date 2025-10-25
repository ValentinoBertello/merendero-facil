package com.merendero.facil.service;

import com.merendero.facil.dto.merendero.MerenderoRequestDto;
import com.merendero.facil.dto.merendero.MerenderoResponseDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface MerenderoService {

    List<MerenderoResponseDto> getAllMerenderos();

    List<MerenderoResponseDto> getMerenderosByCloseUbication(Integer km,
                                                             BigDecimal originLatitude,
                                                             BigDecimal originLongitude);

    MerenderoResponseDto getMerenderoById(Long id);

    MerenderoResponseDto getMerenderoByManagerEmail(String managerEmail);

    MerenderoResponseDto createMerenderoWithAccessToken(MerenderoRequestDto merenderoData, String accessToken);

    MerenderoResponseDto deleteMerenderoById(Long merenderoId);
}
