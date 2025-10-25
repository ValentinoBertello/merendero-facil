package com.merendero.facil.dto.merendero;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * Representa la información que se envía desde el servidor al cliente
 * después de crear, actualizar o consultar un Merendero.
 * **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerenderoResponseDto {

    private Long id;
    private String name;

    private String address;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal latitude;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal longitude;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime openingTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime closingTime;

    private Long managerId;

    private String managerEmail;

    private Boolean active;

    private String accessToken;

    private Integer capacity;

    private String daysOpen;

    // Campo calculado: estado actual (abierto/cerrado)
    public boolean isOpenNow() {
        LocalTime now = LocalTime.now();
        return now.isAfter(openingTime) && now.isBefore(closingTime);
    }
}
