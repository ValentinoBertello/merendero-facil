package com.merendero.facil.movement.dto.entry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de solicitud para la creación de una entrada de mercaderia.
 * **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntryRequestDto {
    private Long supplyId;
    private Long merenderoId;
    private BigDecimal quantity;
    private EntryType entryType; // enum DONATION | PURCHASE
    private LocalDate expirationDate;
}
