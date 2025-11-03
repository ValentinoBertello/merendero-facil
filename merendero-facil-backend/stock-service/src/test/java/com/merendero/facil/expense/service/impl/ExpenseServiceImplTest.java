package com.merendero.facil.expense.service.impl;

import com.merendero.facil.expense.dto.ExpenseRequestDto;
import com.merendero.facil.expense.dto.ExpenseResponseDto;
import com.merendero.facil.expense.entity.ExpenseEntity;
import com.merendero.facil.expense.entity.ExpenseTypeEntity;
import com.merendero.facil.expense.mapper.ExpenseDataMapper;
import com.merendero.facil.expense.repository.ExpenseRepository;
import com.merendero.facil.expense.repository.ExpenseTypeRepository;
import com.merendero.facil.helper.ExpenseTestHelper;
import com.merendero.facil.movement.repository.EntryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {
    @Mock
    private EntryRepository entryRepository;
    @Mock
    private ExpenseDataMapper expenseDataMapper;
    @Mock
    private ExpenseTypeRepository typeRepository;
    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    @Test
    void createExpense() {
        ExpenseRequestDto request = mock(ExpenseRequestDto.class);
        ExpenseEntity createdEntity = ExpenseTestHelper.EXPENSE_1;
        ExpenseResponseDto expectedDto = mock(ExpenseResponseDto.class);

        ExpenseServiceImpl spyService = spy(expenseService);
        doReturn(createdEntity).when(spyService).createExpenseEntity(request);
        when(expenseDataMapper.mapExpenseEntityToExpenseResponse(createdEntity)).thenReturn(expectedDto);

        ExpenseResponseDto actual = spyService.createExpense(request);

        assertSame(expectedDto, actual);
        verify(spyService, times(1)).createExpenseEntity(request);
        verify(expenseDataMapper, times(1)).mapExpenseEntityToExpenseResponse(createdEntity);
    }

    @Test
    void createExpenseEntity() {
        ExpenseRequestDto req = mock(ExpenseRequestDto.class);
        when(req.getTypeExpenseId()).thenReturn(ExpenseTestHelper.TYPE_COMPRA_INSUMOS.getId());
        when(req.getEntryId()).thenReturn(ExpenseTestHelper.ENTRY_ARROZ.getId());

        when(typeRepository.findById(ExpenseTestHelper.TYPE_COMPRA_INSUMOS.getId()))
                .thenReturn(Optional.of(ExpenseTestHelper.TYPE_COMPRA_INSUMOS));
        when(entryRepository.findById(ExpenseTestHelper.ENTRY_ARROZ.getId()))
                .thenReturn(Optional.of(ExpenseTestHelper.ENTRY_ARROZ));

        when(expenseDataMapper.mapExpenseRequestToExpenseEntity(req,
                ExpenseTestHelper.TYPE_COMPRA_INSUMOS,
                ExpenseTestHelper.ENTRY_ARROZ))
                .thenReturn(ExpenseTestHelper.EXPENSE_1);

        when(expenseRepository.save(ExpenseTestHelper.EXPENSE_1)).thenReturn(ExpenseTestHelper.EXPENSE_1);

        ExpenseEntity result = expenseService.createExpenseEntity(req);

        assertSame(ExpenseTestHelper.EXPENSE_1, result);
    }

    @Test
    void getExpensesFromMerendero() {
        Long merenderoId = 1L;
        List<ExpenseEntity> entities = ExpenseTestHelper.ALL_EXPENSES;
        List<ExpenseResponseDto> dtos = List.of(mock(ExpenseResponseDto.class), mock(ExpenseResponseDto.class));

        when(expenseRepository.findByMerenderoId(merenderoId)).thenReturn(entities);
        when(expenseDataMapper.mapExpenseEntitiesToExpenseResponses(entities)).thenReturn(dtos);

        List<ExpenseResponseDto> result = expenseService.getExpensesFromMerendero(merenderoId);

        assertEquals(dtos, result);
        verify(expenseRepository).findByMerenderoId(merenderoId);
        verify(expenseDataMapper).mapExpenseEntitiesToExpenseResponses(entities);
    }

    @Test
    void getExpenseTypes() {
        List<ExpenseTypeEntity> types = List.of(
                ExpenseTestHelper.TYPE_COMPRA_INSUMOS,
                ExpenseTestHelper.TYPE_LUZ_GAS,
                ExpenseTestHelper.TYPE_OTROS
        );
        when(typeRepository.findAll()).thenReturn(types);

        List<ExpenseTypeEntity> result = expenseService.getExpenseTypes();

        assertEquals(types, result);
        verify(typeRepository).findAll();
    }

    @Test
    void createExpenseEntity_typeNotFound_throwsEntityNotFound() {
        Long missingTypeId = 99L;
        ExpenseRequestDto req = mock(ExpenseRequestDto.class);
        when(req.getTypeExpenseId()).thenReturn(missingTypeId);

        when(typeRepository.findById(missingTypeId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> expenseService.createExpenseEntity(req));

        assertEquals(String.format("Tipo de gasto con ID %d no encontrado", missingTypeId), ex.getMessage());

        verify(typeRepository).findById(missingTypeId);
        verifyNoInteractions(entryRepository, expenseRepository, expenseDataMapper);
    }

    @Test
    void createExpenseEntity_entryNotFound_throwsEntityNotFound() {
        Long typeId = ExpenseTestHelper.TYPE_COMPRA_INSUMOS.getId();
        Long missingEntryId = ExpenseTestHelper.ENTRY_ARROZ.getId();

        ExpenseRequestDto req = mock(ExpenseRequestDto.class);
        when(req.getTypeExpenseId()).thenReturn(typeId);
        when(req.getEntryId()).thenReturn(missingEntryId);

        when(typeRepository.findById(typeId)).thenReturn(Optional.of(ExpenseTestHelper.TYPE_COMPRA_INSUMOS));
        when(entryRepository.findById(missingEntryId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> expenseService.createExpenseEntity(req));

        assertEquals(String.format("Entrada con ID %d no encontrado", missingEntryId), ex.getMessage());
    }
}