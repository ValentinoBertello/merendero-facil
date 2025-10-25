package com.merendero.facil.movement.dto.entry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa la información que se envía desde el servidor al cliente
 * después de crear, actualizar o consultar una entrada de insumos.
 * **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntryResponseDto {
    private Long id;
    private Long supplyId;
    private String supplyName;
    private String category;
    private String unit;
    private BigDecimal quantity;
    private LocalDateTime entryDate;
    private String entryType; //DONATION | PURCHASE
    private BigDecimal cost;
}