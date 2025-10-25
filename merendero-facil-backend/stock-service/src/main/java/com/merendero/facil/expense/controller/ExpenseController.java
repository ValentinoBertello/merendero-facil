package com.merendero.facil.expense.controller;

import com.merendero.facil.expense.dto.ExpenseRequestDto;
import com.merendero.facil.expense.dto.ExpenseResponseDto;
import com.merendero.facil.expense.entity.ExpenseTypeEntity;
import com.merendero.facil.expense.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(originPatterns = "*")
@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    /**
     * POST /expenses
     * Crea y guarda un nuevo gasto asociado a un merendero específico.
     **/
    @PostMapping
    @PreAuthorize("@merenderoSecurity.isOwner(#expenseRequestDto.merenderoId, authentication.name)")
    public ResponseEntity<ExpenseResponseDto> saveExpense(@RequestBody ExpenseRequestDto expenseRequestDto) {
        return ResponseEntity.ok(expenseService.createExpense(expenseRequestDto));
    }

    /**
     * GET /expenses/{merenderoId}
     * Trae todos los gastos asociados a un merendero específico.
     **/
    @GetMapping("/{merenderoId}")
    @PreAuthorize("@merenderoSecurity.isOwner(#merenderoId, authentication.name)")
    public ResponseEntity<List<ExpenseResponseDto>> getExpensesFromMerendero(@PathVariable Long merenderoId) {
        return ResponseEntity.ok(expenseService.getExpensesFromMerendero(merenderoId));
    }

    /**
     * GET /expenses/types
     * Trae todos los tipos de gastos (Compra insumos, Luz y Agua, etc)
     **/
    @GetMapping("/types")
    public ResponseEntity<List<ExpenseTypeEntity>> getExpenseTypes() {
        return ResponseEntity.ok(expenseService.getExpenseTypes());
    }
}