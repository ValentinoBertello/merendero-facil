package com.merendero.facil.stock.dto;

import com.merendero.facil.common.enums.Unit;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Dto enviado al cliente que representa el stock agregado por insumo:
 * Incluye cantidad total disponible, próxima fecha de vto, etc.
 * **/
@Data
public class ItemStockDto {
    private Long supplyId;
    private String supplyName;
    private BigDecimal minQuantity;
    private Unit unit;
    private String category;
    private BigDecimal totalStock;
    private LocalDate nextExpiration;

    public ItemStockDto(Long supplyId, String supplyName, BigDecimal minQuantity,
                        Unit unit, String category, Number totalStock,
                        LocalDate nextExpiration) {
        this.supplyId = supplyId;
        this.supplyName = supplyName;
        this.minQuantity = minQuantity;
        this.unit = unit;
        this.category = category;
        this.totalStock = totalStock != null ? new BigDecimal(totalStock.toString()) : BigDecimal.ZERO;
        this.nextExpiration = nextExpiration;
    }
}