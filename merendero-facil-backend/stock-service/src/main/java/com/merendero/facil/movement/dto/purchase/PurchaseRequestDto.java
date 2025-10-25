package com.merendero.facil.movement.dto.purchase;

import com.merendero.facil.expense.dto.ExpenseRequestDto;
import com.merendero.facil.movement.dto.entry.EntryRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de solicitud para la creación de una compra de insumos (entry + expense)
 * **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRequestDto {
    EntryRequestDto entryRequestDto;
    ExpenseRequestDto expenseRequestDto;
}
