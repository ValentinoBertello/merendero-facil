package com.merendero.facil.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa la información que se envía desde el servidor al cliente
 * después de crear, actualizar o consultar un gasto.
 * **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponseDto {
    private Long id;
    private BigDecimal amount;
    private Long merenderoId;
    private String type;
    private LocalDateTime expenseDate;

    // Pueden ser nulos
    private Long supplyId;
    private String supplyName;
    private BigDecimal quantity;
    private String unit;
}
