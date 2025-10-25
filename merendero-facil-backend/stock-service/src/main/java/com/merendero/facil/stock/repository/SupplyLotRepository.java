package com.merendero.facil.stock.repository;

import com.merendero.facil.stock.dto.ItemStockDto;
import com.merendero.facil.stock.entity.SupplyLotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SupplyLotRepository extends JpaRepository<SupplyLotEntity, Long> {

    /**
     * Eliminamos todos los lotes de un insumo específico.
     **/
    void deleteByEntry_Supply_Id(Long supplyId);

    /**
     * Traemos los lotes de un insumo específico ordenados por fecha de
     * vencimiento ascendente.
     **/
    @Query("SELECT l FROM SupplyLotEntity l WHERE l.entry.supply.id = :supplyId" +
            " AND l.currentQuantity  > 0 ORDER BY l.expirationDate ASC")
    List<SupplyLotEntity> findLotsBySupplyId(Long supplyId);

    /**
     * Suma la cantidad actual (`currentQuantity`) de todos los lotes de un
     * insumo en particular. Dándonos así, su stock actual.
     **/
    @Query("SELECT COALESCE(SUM(l.currentQuantity), 0) " +
            "FROM SupplyLotEntity l " +
            "WHERE l.entry.supply.id = :supplyId ")
    BigDecimal sumTotalStockFromSupply(Long supplyId);

    /**
     * Obtiene la información de stock de cada insumo, construyendo una lista
     * de `ItemStockDto`. Sumando `currentQuantity` y eligiendo la mínima `expirationDate` por inusmo.
     *
     * Se Usa LEFT JOIN para incluir insumos sin lotes (totalStock = 0).
     **/
    @Query("SELECT new com.merendero.facil.stock.dto.ItemStockDto(" +
            "s.id, s.name, s.minQuantity, s.unit, s.supplyCategory.name, " +
            "COALESCE(SUM(l.currentQuantity), 0), " +
            "MIN(l.expirationDate)) " +
            "FROM SupplyEntity s " +
            "LEFT JOIN EntryEntity e ON e.supply.id = s.id " +
            "LEFT JOIN SupplyLotEntity l ON l.entry.id = e.id " +
            "WHERE s.merenderoId = :merenderoId AND s.active = true " +
            "GROUP BY s.id, s.name, s.minQuantity, s.unit, s.supplyCategory.name")
    List<ItemStockDto> findStockItemsByMerendero(@Param("merenderoId") Long merenderoId);
}
