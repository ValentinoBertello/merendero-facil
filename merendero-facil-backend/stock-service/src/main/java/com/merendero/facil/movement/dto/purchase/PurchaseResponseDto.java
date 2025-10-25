package com.merendero.facil.movement.dto.purchase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa la información que se envía desde el servidor al cliente
 * después de crear la compra de un insumo (entry + expense)
 * **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseResponseDto {
    Long entryId;
    Long expenseId;
}
