package com.merendero.facil.movement.service.entry.impl;

import com.merendero.facil.expense.entity.ExpenseEntity;
import com.merendero.facil.expense.repository.ExpenseRepository;
import com.merendero.facil.movement.dto.entry.EntryRequestDto;
import com.merendero.facil.movement.dto.entry.EntryResponseDto;
import com.merendero.facil.movement.entity.EntryEntity;
import com.merendero.facil.movement.mapper.EntryDataMapper;
import com.merendero.facil.movement.repository.EntryRepository;
import com.merendero.facil.movement.service.entry.EntryService;
import com.merendero.facil.stock.service.LotService;
import com.merendero.facil.supply.entity.SupplyEntity;
import com.merendero.facil.supply.repository.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Toda la lógica de negocio referida a la entrada de insumos.
 **/
@Service
@RequiredArgsConstructor
public class EntryServiceImpl implements EntryService {

    private final ExpenseRepository expenseRepository;
    private final LotService lotService;
    private final SupplyRepository supplyRepository;
    private final EntryRepository entryRepository;
    private final EntryDataMapper entryDataMapper;

    /**
     * Recupera las entradas de insumo de un merendero en específico.
     *
     * Algunas entradas corresponden a donaciones (no generan gasto) y otras
     * corresponden a compras (tienen un `expense` asociado).
     *
     * Por lo que debemos saber, por cada entrada, si existe un costo asociado.
     **/
    @Override
    public List<EntryResponseDto> getEntriesFromMerendero(Long merenderoId) {
        //Obtener todas las entradas del merendero
        List<EntryEntity> entryEntities = this.entryRepository.findByMerenderoId(merenderoId);

        // Obtener los ids de las entradas
        List<Long> entryIds = buildEntryIds(entryEntities);

        // Obtener map con los entryId (que tienen un gasto asociado) y su costo
        Map<Long, BigDecimal> entryWithCostMap = buildEntryCostMap(entryIds);

        // Mapear a Dtos
        return entryEntities.stream().map(entry -> {
            EntryResponseDto responseDto = this.entryDataMapper.mapEntryEntityToEntryResponse(entry);
            // Si la entrada tiene un gasto asociado en el mapa, le seteamos ese costo
            if (entryWithCostMap.containsKey(entry.getId())) {
                responseDto.setCost(entryWithCostMap.get(entry.getId()));
            }
            return responseDto;
        }).collect(Collectors.toList());
    }

    /**
     * Crea una nueva entrada a partir del DTO recibido y devuelve su representación en DTO.
     * Delega en createEntryEntity para la lógica de persistencia.
     **/
    @Override
    @Transactional
    public EntryResponseDto createEntry(EntryRequestDto entryRequestDto) {
        EntryEntity entry = this.createEntryEntity(entryRequestDto);
        return this.entryDataMapper.mapEntryEntityToEntryResponse(entry);
    }

    /**
     * Crea y persiste una nueva EntryEntity a partir de los datos del requestDto.
     * Además crea el lote correspondiente a la nueva entrada
     * **/
    @Override
    @Transactional
    public EntryEntity createEntryEntity(EntryRequestDto entryRequestDto) {
        this.validateExpirationDateIfPresent(entryRequestDto.getExpirationDate());
        SupplyEntity supply = this.findSupplyOrThrow(entryRequestDto.getSupplyId());

        EntryEntity entryEntity = this.entryDataMapper.mapEntryRequestToEntryEntity(entryRequestDto, supply);
        entryEntity = this.entryRepository.save(entryEntity);

        this.lotService.createLotFromEntry(entryEntity, entryRequestDto.getExpirationDate());
        return entryEntity;
    }

    /**
     * Valida que la fecha de vencimiento no sea anterior a la fecha actual.
     */
    private void validateExpirationDateIfPresent(LocalDate expirationDate) {
        LocalDate today = LocalDate.now();
        if (expirationDate.isBefore(today)) {
            throw new IllegalArgumentException("Fecha de vencimiento inválida: " + expirationDate);
        }
    }

    /**
     * Busca un Supply por id y lanza IllegalArgumentException si no existe.
     */
    private SupplyEntity findSupplyOrThrow(Long supplyId) {
        return supplyRepository.findById(supplyId)
                .orElseThrow(() -> new IllegalArgumentException("Insumo no encontrado con id: " + supplyId));
    }

    /**
     * Retornamos lista de ids a partir de una lista de `EntryEntity`.
     */
    private List<Long> buildEntryIds(List<EntryEntity> entries) {
        return entries.stream()
                .map(EntryEntity::getId)
                .collect(Collectors.toList());
    }

    /**
     * Construye un mapa que relaciona el id de una entrada con el monto del gasto asociado.
     */
    private Map<Long, BigDecimal> buildEntryCostMap(List<Long> entryIds) {
        return this.expenseRepository.findByEntryIdIn(entryIds)
                .stream()
                .collect(Collectors.toMap(
                        exp -> exp.getEntry().getId(),
                        ExpenseEntity::getAmount
                ));
    }
}