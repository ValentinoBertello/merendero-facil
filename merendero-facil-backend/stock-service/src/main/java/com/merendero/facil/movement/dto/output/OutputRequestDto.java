package com.merendero.facil.movement.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de solicitud para la creación de una salida de insumos.
 * **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutputRequestDto {
    private Long merenderoId;
    private Long supplyId;
    private BigDecimal quantity;
}