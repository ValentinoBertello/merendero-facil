package com.merendero.facil.expense.service.impl;

import com.merendero.facil.expense.dto.ExpenseRequestDto;
import com.merendero.facil.expense.dto.ExpenseResponseDto;
import com.merendero.facil.expense.entity.ExpenseEntity;
import com.merendero.facil.expense.entity.ExpenseTypeEntity;
import com.merendero.facil.expense.mapper.ExpenseDataMapper;
import com.merendero.facil.expense.repository.ExpenseRepository;
import com.merendero.facil.expense.repository.ExpenseTypeRepository;
import com.merendero.facil.expense.service.ExpenseService;
import com.merendero.facil.movement.entity.EntryEntity;
import com.merendero.facil.movement.repository.EntryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Toda la lógica de negocio referida a los gastos.
 **/
@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final EntryRepository entryRepository;
    private final ExpenseDataMapper expenseDataMapper;
    private final ExpenseTypeRepository typeRepository;
    private final ExpenseRepository expenseRepository;

    /**
     * Crea un nuevo gasto a partir del DTO recibido y devuelve su representación en DTO.
     * Delega en createExpenseEntity para la lógica de persistencia.
     **/
    @Override
    public ExpenseResponseDto createExpense(ExpenseRequestDto expenseRequestDto) {
        ExpenseEntity expenseEntity = this.createExpenseEntity(expenseRequestDto);
        return  this.expenseDataMapper.mapExpenseEntityToExpenseResponse(expenseEntity);
    }

    /**
     * Crea y persiste una entidad ExpenseEntity a partir de los datos del DTO.
     * - Si corresponde, busca la entrada asociada.
     */
    @Override
    public ExpenseEntity createExpenseEntity(ExpenseRequestDto expenseRequestDto) {
        ExpenseTypeEntity type = getTypeExpenseById(expenseRequestDto.getTypeExpenseId());
        EntryEntity entry = null;
        if (expenseRequestDto.getEntryId() != null) {
            entry = this.getEntryEntityById(expenseRequestDto.getEntryId());
        }
        ExpenseEntity expenseEntity =
                this.expenseDataMapper.mapExpenseRequestToExpenseEntity(expenseRequestDto, type, entry);
        return this.expenseRepository.save(expenseEntity);
    }

    /**
     * Trae todos los gastos asociados a un merendero específico.
     **/
    @Override
    public List<ExpenseResponseDto> getExpensesFromMerendero(Long merenderoId) {
        List<ExpenseEntity> entities = this.expenseRepository.findByMerenderoId(merenderoId);
        return this.expenseDataMapper.mapExpenseEntitiesToExpenseResponses(entities);
    }

    /**
     * Trae todos los tipos de gastos (compra de insumos/ Mantenimiento/ etc)
     **/
    @Override
    public List<ExpenseTypeEntity> getExpenseTypes() {
        return this.typeRepository.findAll();
    }

    /**
     * Busca un tipo de gasto por su id.
     */
    private ExpenseTypeEntity getTypeExpenseById(Long typeExpenseId) {
        return typeRepository.findById(typeExpenseId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Tipo de gasto con ID %d no encontrado", typeExpenseId)
                ));
    }

    /**
     * Busca una entrada por su id.
     */
    private EntryEntity getEntryEntityById(Long entryId) {
        return entryRepository.findById(entryId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Entrada con ID %d no encontrado", entryId)
                ));
    }
}