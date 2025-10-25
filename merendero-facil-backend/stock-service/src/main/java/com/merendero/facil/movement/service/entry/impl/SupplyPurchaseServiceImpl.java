package com.merendero.facil.movement.service.entry.impl;

import com.merendero.facil.expense.dto.ExpenseRequestDto;
import com.merendero.facil.expense.entity.ExpenseEntity;
import com.merendero.facil.expense.service.ExpenseService;
import com.merendero.facil.movement.dto.purchase.PurchaseRequestDto;
import com.merendero.facil.movement.dto.purchase.PurchaseResponseDto;
import com.merendero.facil.movement.entity.EntryEntity;
import com.merendero.facil.movement.service.entry.EntryService;
import com.merendero.facil.movement.service.entry.SupplyPurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupplyPurchaseServiceImpl implements SupplyPurchaseService {

    private final EntryService entryService;
    private final ExpenseService expenseService;

    /**
     * Registra una nueva compra de insumos, sería la inserción al mismo tiempo
     * de un Entrada de insumos y un gasto. Ambos asociados.
     **/
    @Override
    public PurchaseResponseDto createPurchase(PurchaseRequestDto dto) {
        // Crear entry (EntryService maneja validaciones de expiración, supply, lote, etc.)
        EntryEntity createdEntry = entryService.createEntryEntity(dto.getEntryRequestDto());

        // Preparar y crear expense asociando el entryId recién creado
        ExpenseRequestDto expenseDto = dto.getExpenseRequestDto();
        expenseDto.setEntryId(createdEntry.getId());

        ExpenseEntity createdExpense = expenseService.createExpenseEntity(expenseDto);

        // Respuesta
        return PurchaseResponseDto.builder()
                .entryId(createdEntry.getId())
                .expenseId(createdExpense.getId())
                .build();
    }
}