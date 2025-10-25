package com.merendero.facil.supply.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Representa la información que se envía desde el servidor al cliente
 * después de crear, actualizar o consultar un Insumo.
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyResponseDto {
    private Long id;
    private String name;
    private String unit;
    private BigDecimal minQuantity;
    private Long merenderoId;
    private Long categoryId;
}
