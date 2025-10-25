package com.merendero.facil.expense.mapper;

import com.merendero.facil.expense.dto.ExpenseRequestDto;
import com.merendero.facil.expense.dto.ExpenseResponseDto;
import com.merendero.facil.expense.entity.ExpenseEntity;
import com.merendero.facil.expense.entity.ExpenseTypeEntity;
import com.merendero.facil.movement.entity.EntryEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * La clase "ExpenseDataMapper" se encarga de mapear gastos "request", "entity" y "response"
 * entre ellos.
 */
@Component
public class ExpenseDataMapper {

    /**
     * Mapea un DTO de petición {@link ExpenseRequestDto} a una nueva entidad {@link ExpenseEntity}.
     */
    public ExpenseEntity mapExpenseRequestToExpenseEntity(ExpenseRequestDto requestDTO,
                                                          ExpenseTypeEntity typeEntity,
                                                          EntryEntity entryEntity) {
        return ExpenseEntity.builder()
                .merenderoId(requestDTO.getMerenderoId())
                .amount(requestDTO.getAmount())
                .entry(entryEntity)
                .type(typeEntity)
                .build();
    }

    /**
     * Mapea una entidad {@link ExpenseEntity} a un dto {@link ExpenseResponseDto}.
     */
    public ExpenseResponseDto mapExpenseEntityToExpenseResponse(ExpenseEntity expenseEntity) {
        ExpenseResponseDto response = ExpenseResponseDto.builder()
                .id(expenseEntity.getId())
                .merenderoId(expenseEntity.getMerenderoId())
                .amount(expenseEntity.getAmount())
                .type(expenseEntity.getType().getDescription())
                .expenseDate(expenseEntity.getExpenseDate())
                .build();
        if (Objects.equals(expenseEntity.getType().getDescription(), "Compra de Insumos")) {
            response.setSupplyId(expenseEntity.getEntry().getSupply().getId());
            response.setQuantity(expenseEntity.getEntry().getQuantity());
            response.setSupplyName(expenseEntity.getEntry().getSupply().getName());
            response.setUnit(expenseEntity.getEntry().getSupply().getUnit().name());
        } else {
            response.setQuantity(BigDecimal.ZERO);
            response.setSupplyId(0L);
            response.setSupplyName("NO-APLICA");
            response.setUnit("NO-APLICA");
        }
        return response;
    }

    /**
     * Mapea una lista de entidades a una de dtos.
     */
    public List<ExpenseResponseDto> mapExpenseEntitiesToExpenseResponses
            (List<ExpenseEntity> expenseEntities) {
        List<ExpenseResponseDto> responses = new ArrayList<>();
        for (ExpenseEntity eE : expenseEntities){
            responses.add(this.mapExpenseEntityToExpenseResponse(eE));
        }
        return responses;
    }
}