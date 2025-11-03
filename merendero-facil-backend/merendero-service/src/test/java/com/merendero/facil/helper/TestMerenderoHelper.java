package com.merendero.facil.helper;

import com.merendero.facil.dto.merendero.MerenderoRequestDto;
import com.merendero.facil.entities.MerenderoEntity;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

public class TestMerenderoHelper {

    public static final MerenderoEntity MERENDERO_1 = MerenderoEntity.builder()
            .id(1L)
            .name("Merendero 1")
            .address("Tokio 170, X5152 Villa Carlos Paz, Córdoba")
            .latitude(new BigDecimal("-31.407871765806295"))
            .longitude(new BigDecimal("-64.47402030890402"))
            .capacity(100)
            .daysOpen("Lunes-Viernes")
            .openingTime(LocalTime.of(9, 0))
            .closingTime(LocalTime.of(18, 0))
            .managerId(101L)
            .managerEmail("manager1@merendero.com")
            .active(true)
            .donations(Collections.emptyList())
            .accessToken("acces_token_1234")
            .build();

    public static final MerenderoEntity MERENDERO_2 = MerenderoEntity.builder()
            .id(2L)
            .name("Merendero 2")
            .address("Zuviria 280, X5152 Villa Carlos Paz, Córdoba")
            .latitude(new BigDecimal("-31.408579572675727"))
            .longitude(new BigDecimal("-64.47499476287219"))
            .capacity(120)
            .daysOpen("Lunes-Sábado")
            .openingTime(LocalTime.of(8, 30))
            .closingTime(LocalTime.of(17, 30))
            .managerId(102L)
            .managerEmail("manager2@merendero.com")
            .active(true)
            .donations(Collections.emptyList())
            .build();

    public static final MerenderoEntity MERENDERO_3 = MerenderoEntity.builder()
            .id(3L)
            .name("Merendero 3")
            .address("Trejo y Zanabria 288, X5152 Villa Carlos Paz, Córdoba")
            .latitude(new BigDecimal("-31.409710"))
            .longitude(new BigDecimal("-64.476154"))
            .capacity(80)
            .daysOpen("Martes-Domingo")
            .openingTime(LocalTime.of(10, 0))
            .closingTime(LocalTime.of(19, 0))
            .managerId(103L)
            .managerEmail("manager3@merendero.com")
            .active(true)
            .donations(Collections.emptyList())
            .build();

    public static final MerenderoEntity MERENDERO_4 = MerenderoEntity.builder()
            .id(4L)
            .name("Merendero 4")
            .address("Villalobos 270, X5152 Villa Carlos Paz, Córdoba")
            .latitude(new BigDecimal("-31.41028213895771"))
            .longitude(new BigDecimal("-64.47702927254647"))
            .capacity(60)
            .daysOpen("Lunes-Viernes")
            .openingTime(LocalTime.of(9, 0))
            .closingTime(LocalTime.of(17, 0))
            .managerId(104L)
            .managerEmail("manager4@merendero.com")
            .active(true)
            .donations(Collections.emptyList())
            .build();

    public static final MerenderoEntity MERENDERO_5 = MerenderoEntity.builder()
            .id(5L)
            .name("Merendero 5")
            .address("Fleming 241, X5152 Villa Carlos Paz, Córdoba")
            .latitude(new BigDecimal("-31.41083577565834"))
            .longitude(new BigDecimal("-64.47778443020928"))
            .capacity(90)
            .daysOpen("Lunes-Sábado")
            .openingTime(LocalTime.of(8, 0))
            .closingTime(LocalTime.of(16, 0))
            .managerId(105L)
            .managerEmail("manager5@merendero.com")
            .active(true)
            .donations(Collections.emptyList())
            .build();

    public static final List<MerenderoEntity> MERENDEROS = List.of(
            MERENDERO_1,
            MERENDERO_2,
            MERENDERO_3,
            MERENDERO_4,
            MERENDERO_5
    );

    public static final MerenderoRequestDto MERENDERO_REQUEST_1 = MerenderoRequestDto.builder()
            .name("Merendero 1")
            .address("Tokio 170, X5152 Villa Carlos Paz, Córdoba")
            .latitude(new BigDecimal("-31.407871765806295"))
            .longitude(new BigDecimal("-64.47402030890402"))
            .capacity(50)
            .daysOpen("Lunes a Viernes")
            .openingTime(LocalTime.of(8, 0))
            .closingTime(LocalTime.of(17, 0))
            .createdUser(1L)
            .managerEmail("valen@gmail.com")
            .build();
}