package com.merendero.facil.stock.service.impl;

import com.merendero.facil.movement.entity.EntryEntity;
import com.merendero.facil.movement.entity.OutputEntity;
import com.merendero.facil.stock.dto.ItemStockDto;
import com.merendero.facil.stock.dto.LotDto;
import com.merendero.facil.stock.entity.SupplyLotEntity;
import com.merendero.facil.stock.mapper.LotDataMapper;
import com.merendero.facil.stock.repository.SupplyLotRepository;
import com.merendero.facil.stock.service.LotService;
import com.merendero.facil.supply.entity.SupplyEntity;
import com.merendero.facil.supply.repository.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Toda la lógica de negocio referida a los lotes de los insumos
 * y al stock en general.
 **/
@Service
@RequiredArgsConstructor
public class LotServiceImpl implements LotService {

    private final LotDataMapper lotDataMapper;
    private final SupplyLotRepository lotRepository;
    private final SupplyRepository supplyRepository;

    /**
     * Crea y persiste un lote a partir de la nueva entrada de un insumo.
     */
    @Override
    public SupplyLotEntity createLotFromEntry(EntryEntity entryEntity, LocalDate expirationDate) {
        SupplyLotEntity lotEntity = lotDataMapper.createLotFromEntryRequest(entryEntity, expirationDate);
        return lotRepository.save(lotEntity);
    }

    /**
     * Método llamado luego del registro de una salida de insumos.
     * Deduce la cantidad de stock de los lotes (FIFO por fecha de expiración).
     */
    @Override
    @Transactional
    public void deductFromLots(OutputEntity outputEntity) {
        // Obtenemos los lotes ordenados por fecha de expiración
        List<SupplyLotEntity> lots = this.lotRepository.findLotsBySupplyId(outputEntity.getSupply().getId());

        BigDecimal remaining = outputEntity.getQuantity(); // Cantidad a cubrir
        for(SupplyLotEntity l : lots)  {
            // Si ya no queda nada por descontar nos salimos del bucle
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            // Si el lote tiene suficiente cantidad para cubrir lo que falta:
            if (remaining.compareTo(l.getCurrentQuantity()) <= 0) {
                // restamos la cantidad necesaria y actualizamod el lote
                l.setCurrentQuantity(l.getCurrentQuantity().subtract(remaining));
                this.lotRepository.save(l);
                remaining = BigDecimal.ZERO;
            }
            // Si el lote no alcanza para cubrir lo que falta:
            if (remaining.compareTo(l.getCurrentQuantity()) > 0) {
                // consumimos el lote completo y lo eliminamos
                remaining = remaining.subtract(l.getCurrentQuantity());
                this.lotRepository.delete(l);
            }
        }

        this.ensureStockCovered(remaining); // Lanzamos excepción controlada si quedó cantidad a cubrir
    }

    /**
     * Obtiene la suma total del stock disponible de un insumo.
     * Delegado al repositorio que agrupa la suma de currentQuantity de los lotes.
     */
    @Override
    public BigDecimal getTotalStockBySupply(Long merenderoId, Long supplyId) {
        this.validateSupplyBelongsToMerendero(supplyId, merenderoId);
        return this.lotRepository.sumTotalStockFromSupply(supplyId);
    }

    /**
     * Obtiene los lotes activos de un insumo y los mapea a DTOs.
     */
    @Override
    public List<LotDto> getLotsBySupply(Long merenderoId, Long supplyId) {
        this.validateSupplyBelongsToMerendero(supplyId, merenderoId);
        List<SupplyLotEntity> lots = this.lotRepository.findLotsBySupplyId(supplyId);
        return this.mapLotEntitiesToLotDtos(lots);
    }

    /**
     * Devuelve el inventario completo de un merendero, mediante una lista de `ItemStockDto`.
     *
     * Los {@link ItemStockDto} se construyen mediante una consulta agregada en el repositorio
     * trayendo la cantidad de stock total y la fecha de expiracion mas pronta de cada insumo.
     */
    @Override
    public List<ItemStockDto> getStockItems(Long merenderoId) {
        return this.lotRepository.findStockItemsByMerendero(merenderoId);
    }

    /**
     * Verifica si hay stock suficiente para la cantidad de un insumo específico
     */
    @Override
    public Boolean checkSupplyStock(Long merenderoId, Long supplyId, BigDecimal quantity) {
        BigDecimal totalStock = this.getTotalStockBySupply(merenderoId, supplyId);
        return totalStock.compareTo(quantity) >= 0;
    }

    /**
     * Lanza una excepción si queda cantidad pendiente por deducir.
     */
    private void ensureStockCovered(BigDecimal remaining) {
        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Stock insuficiente.");
        }
    }

    /**
     * Mapea una lista de entidades de lote a sus DTO correspondientes
     * e inyecta en cada DTO el campo calculado `daysToExpire`.
     */
    private List<LotDto> mapLotEntitiesToLotDtos(List<SupplyLotEntity> entities) {
        List<LotDto> result = new ArrayList<>();
        for (SupplyLotEntity e : entities) {
            LotDto dto = this.lotDataMapper.createLotDtoFromLotEntity(e);
            long dias = ChronoUnit.DAYS.between(LocalDate.now(), e.getExpirationDate());
            dto.setDaysToExpire((int) dias);
            result.add(dto);
        }
        return result;
    }

    /**
     * Valida que el insumo exista y pertenezca al merendero
     */
    private void validateSupplyBelongsToMerendero(Long supplyId, Long merenderoId) {
        SupplyEntity supply = supplyRepository.findById(supplyId)
                .orElseThrow(() -> new RuntimeException("Insumo no encontrado con id: " + supplyId));

        if (!supply.getMerenderoId().equals(merenderoId)) {
            throw new RuntimeException("El insumo no pertenece al merendero");
        }
    }
}