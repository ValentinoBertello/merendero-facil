package com.merendero.facil.movement.service.entry.impl;

import com.merendero.facil.expense.dto.ExpenseRequestDto;
import com.merendero.facil.expense.entity.ExpenseEntity;
import com.merendero.facil.expense.service.ExpenseService;
import com.merendero.facil.movement.dto.entry.EntryRequestDto;
import com.merendero.facil.movement.dto.purchase.PurchaseRequestDto;
import com.merendero.facil.movement.dto.purchase.PurchaseResponseDto;
import com.merendero.facil.movement.entity.EntryEntity;
import com.merendero.facil.movement.service.entry.EntryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.merendero.facil.helper.MovementTestHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplyPurchaseServiceImplTest {
    @Mock
    private EntryService entryService;
    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private SupplyPurchaseServiceImpl supplyPurchaseService;

    @Test
    void createPurchase() {
        // Arrange
        EntryEntity entry = ENTRY_1;
        ExpenseEntity expense = ExpenseEntity.builder().id(99L).build();

        EntryRequestDto entryDto = mock(EntryRequestDto.class);
        ExpenseRequestDto expenseDto = ExpenseRequestDto.builder().build();
        PurchaseRequestDto request = PurchaseRequestDto.builder()
                .entryRequestDto(entryDto)
                .expenseRequestDto(expenseDto)
                .build();

        when(entryService.createEntryEntity(entryDto)).thenReturn(entry);
        when(expenseService.createExpenseEntity(expenseDto)).thenReturn(expense);

        PurchaseResponseDto result = supplyPurchaseService.createPurchase(request);

        assertEquals(entry.getId(), result.getEntryId());
        assertEquals(expense.getId(), result.getExpenseId());
        verify(entryService).createEntryEntity(entryDto);
        verify(expenseService).createExpenseEntity(expenseDto);
    }
}