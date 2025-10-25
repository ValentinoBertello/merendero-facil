package com.merendero.facil.expense.service;

import com.merendero.facil.expense.dto.ExpenseRequestDto;
import com.merendero.facil.expense.dto.ExpenseResponseDto;
import com.merendero.facil.expense.entity.ExpenseEntity;
import com.merendero.facil.expense.entity.ExpenseTypeEntity;

import java.util.List;

public interface ExpenseService {

    ExpenseResponseDto createExpense(ExpenseRequestDto expenseRequestDto);

    ExpenseEntity createExpenseEntity(ExpenseRequestDto expenseRequestDto);

    List<ExpenseResponseDto> getExpensesFromMerendero(Long merenderoId);

    List<ExpenseTypeEntity> getExpenseTypes();
}
