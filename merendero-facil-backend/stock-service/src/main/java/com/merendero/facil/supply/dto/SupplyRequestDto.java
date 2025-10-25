package com.merendero.facil.supply.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de solicitud para la creación de un Insumo.
 * **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyRequestDto {
    private String name;
    private String unit;
    private BigDecimal minQuantity;
    private LocalDate lastAlertDate;
    private Long supplyCategoryId;
}
