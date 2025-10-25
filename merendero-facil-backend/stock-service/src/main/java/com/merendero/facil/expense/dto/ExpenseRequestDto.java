package com.merendero.facil.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de solicitud para la creación de un gasto.
 * **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseRequestDto {
    private Long merenderoId;
    private BigDecimal amount;
    private Long typeExpenseId;

    // Puede ser nulos
    private Long entryId;
}
