package com.merendero.facil.supply.service.impl;

import com.merendero.facil.stock.repository.SupplyLotRepository;
import com.merendero.facil.supply.dto.SupplyRequestDto;
import com.merendero.facil.supply.dto.SupplyResponseDto;
import com.merendero.facil.supply.entity.SupplyCategoryEntity;
import com.merendero.facil.supply.entity.SupplyEntity;
import com.merendero.facil.supply.mapper.SupplyDataMapper;
import com.merendero.facil.supply.repository.SupplyCategoryRepository;
import com.merendero.facil.supply.repository.SupplyRepository;
import com.merendero.facil.supply.service.SupplyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Toda la lógica de negocio referida a los Insumos.
 **/
@Service
@RequiredArgsConstructor
public class SupplyServiceImpl implements SupplyService {

    private final SupplyLotRepository lotRepository;
    private final SupplyDataMapper supplyDataMapper;
    private final SupplyRepository supplyRepository;
    private final SupplyCategoryRepository categoryRepository;

    /**
     * Crea y guarda un nuevo insumo asociado a un merendero específico.
     **/
    @Override
    public SupplyResponseDto saveSupply(Long merenderoId, SupplyRequestDto supplyRequestDto) {
        SupplyCategoryEntity category = categoryRepository.findById(supplyRequestDto.getSupplyCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));

        SupplyEntity supplyEntity =
                this.supplyDataMapper.mapSupplyRequestToSupplyEntity(supplyRequestDto, category, merenderoId);
        supplyEntity = this.supplyRepository.save(supplyEntity);

        return this.supplyDataMapper.mapSupplyEntityToSupplyResponse(supplyEntity);
    }

    /**
     * Trae todos los insumos activos asociados a un merendero específico.
     **/
    @Override
    public List<SupplyResponseDto> getSuppliesFromMerendero(Long merenderoId) {
        List<SupplyEntity> supplyEntities = this.supplyRepository.findByMerenderoIdAndActiveTrue(merenderoId);
        return this.supplyDataMapper.mapSupplyEntitiesToSupplyResponses(supplyEntities);
    }

    /**
     * Trae todas las categorías de insumos.
     **/
    @Override
    public List<SupplyCategoryEntity> getSupplyCategories() {
        return this.categoryRepository.findAll();
    }

    /**
     * Elimina un insumo de un merendero específico (marcándolo como inactivo).
     **/
    @Override
    public Long removeSupplyFromMerendero(Long merenderoId, Long supplyId) {
        // Marcar el supply como inactivo
        SupplyEntity supplyEntity = getSupplyOrThrow(supplyId);
        supplyEntity.setActive(false);
        this.supplyRepository.save(supplyEntity);

        // Borramos todos el stock del insumo ya que no será mas tenido en cuenta
        this.lotRepository.deleteByEntry_Supply_Id(supplyId);

        return supplyId;
    }

    /**
     * Busca un SupplyEntity por su id o lanza una excepción si no existe.
     */
    private SupplyEntity getSupplyOrThrow(Long supplyId) {
        return supplyRepository.findById(supplyId)
                .orElseThrow(() -> new EntityNotFoundException("Supply no encontrada con id: " + supplyId));
    }
}