package com.merendero.facil.stock.service.impl;

import com.merendero.facil.helper.MovementTestHelper;
import com.merendero.facil.helper.SupplyLotHelper;
import com.merendero.facil.movement.entity.EntryEntity;
import com.merendero.facil.movement.entity.OutputEntity;
import com.merendero.facil.stock.dto.LotDto;
import com.merendero.facil.stock.entity.SupplyLotEntity;
import com.merendero.facil.stock.mapper.LotDataMapper;
import com.merendero.facil.stock.repository.SupplyLotRepository;
import com.merendero.facil.supply.entity.SupplyEntity;
import com.merendero.facil.supply.repository.SupplyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.merendero.facil.helper.MovementTestHelper.OUTPUT_1;
import static com.merendero.facil.helper.MovementTestHelper.SUPPLY_1;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LotServiceImplTest {
    @Mock
    private LotDataMapper lotDataMapper;
    @Mock
    private SupplyLotRepository lotRepository;
    @Mock
    private SupplyRepository supplyRepository;

    @InjectMocks
    private LotServiceImpl lotService;

    @Test
    void createLotFromEntry() {
        EntryEntity entry = MovementTestHelper.ENTRY_1;
        LocalDate expiration = LocalDate.now().plusDays(30);

        SupplyLotEntity lot = SupplyLotEntity.builder()
                .id(1L)
                .entry(entry)
                .currentQuantity(new BigDecimal("100"))
                .expirationDate(expiration)
                .build();

        when(lotDataMapper.createLotFromEntryRequest(entry, expiration)).thenReturn(lot);
        when(lotRepository.save(lot)).thenReturn(lot);

        SupplyLotEntity result = lotService.createLotFromEntry(entry, expiration);

        assertSame(lot, result);
        verify(lotRepository).save(lot);
    }

    @Test
    void deductFromLots_consumesOneLot() {
        List<SupplyLotEntity> lots = copyLots();

        when(lotRepository.findLotsBySupplyId(1L))
                .thenReturn(lots);
        when(lotRepository.save(any(SupplyLotEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        this.lotService.deductFromLots(OUTPUT_1);

        assertEquals(new BigDecimal("6"), lots.get(0).getCurrentQuantity());
        verify(lotRepository, atLeastOnce()).save(any(SupplyLotEntity.class));
    }

    @Test
    void deductFromLots_consumesMultipleLots() {
        OutputEntity output = OutputEntity.builder()
                .id(1L)
                .quantity(new BigDecimal("40"))
                .supply(SUPPLY_1)
                .build();

        List<SupplyLotEntity> lots = copyLots();

        when(lotRepository.findLotsBySupplyId(1L))
                .thenReturn(lots);
        when(lotRepository.save(any(SupplyLotEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        this.lotService.deductFromLots(output);

        // Primer lote se consume totalmente → delete se debería invocar
        verify(lotRepository).delete(lots.get(0));
        assertEquals(new BigDecimal("50"), lots.get(1).getCurrentQuantity());
        verify(lotRepository, atLeastOnce()).save(any(SupplyLotEntity.class));
    }

    @Test
    void deductFromLots_consumesMultipleEntireLots() {
        OutputEntity output = OutputEntity.builder()
                .id(1L)
                .quantity(new BigDecimal("90"))
                .supply(SUPPLY_1)
                .build();

        List<SupplyLotEntity> lots = copyLots();

        when(lotRepository.findLotsBySupplyId(1L))
                .thenReturn(lots);

        this.lotService.deductFromLots(output);

        // Primer lote se consume totalmente → delete se debería invocar
        verify(lotRepository).delete(lots.get(0));
        verify(lotRepository).delete(lots.get(1));
        assertEquals(new BigDecimal("80"), lots.get(1).getCurrentQuantity());
    }

    @Test
    void deductFromLots_throwsWhenNotEnoughStock() {
        OutputEntity output = OutputEntity.builder()
                .id(1L)
                .quantity(new BigDecimal("500")) // más que el total de currentQuantity
                .supply(SUPPLY_1)
                .build();

        List<SupplyLotEntity> lots = copyLots();

        when(lotRepository.findLotsBySupplyId(1L)).thenReturn(lots);

        assertThrows(RuntimeException.class, () -> lotService.deductFromLots(output));
    }

    @Test
    void getTotalStockBySupply() {
        Long merenderoId = 1L;
        Long supplyId = 2L;

        SupplyEntity supply = SupplyEntity.builder()
                .id(supplyId)
                .merenderoId(merenderoId)
                .build();

        when(supplyRepository.findById(supplyId)).thenReturn(Optional.of(supply));
        when(lotRepository.sumTotalStockFromSupply(supplyId)).thenReturn(new BigDecimal("150"));

        BigDecimal total = lotService.getTotalStockBySupply(merenderoId, supplyId);

        assertEquals(new BigDecimal("150"), total);
        verify(lotRepository).sumTotalStockFromSupply(supplyId);
    }

    @Test
    void getTotalStockBySupply_throwsWhenSupplyNotBelongsToMerendero() {
        Long merenderoId = 1L;
        Long supplyId = 2L;

        SupplyEntity supply = SupplyEntity.builder()
                .id(supplyId)
                .merenderoId(99L)
                .build();

        when(supplyRepository.findById(supplyId)).thenReturn(Optional.of(supply));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> lotService.getTotalStockBySupply(merenderoId, supplyId));

        assertEquals("El insumo no pertenece al merendero", ex.getMessage());
    }

    @Test
    void getLotsBySupply() {
        Long merenderoId = 1L;
        Long supplyId = 1L;

        SupplyEntity supply = MovementTestHelper.SUPPLY_1;
        when(supplyRepository.findById(supplyId)).thenReturn(Optional.of(supply));

        SupplyLotEntity lot = SupplyLotHelper.LOT_1;
        when(lotRepository.findLotsBySupplyId(supplyId)).thenReturn(List.of(lot));

        LotDto dto = new LotDto();
        when(lotDataMapper.createLotDtoFromLotEntity(lot)).thenReturn(dto);

        List<LotDto> result = lotService.getLotsBySupply(merenderoId, supplyId);

        assertEquals(1, result.size());
        assertEquals((int) ChronoUnit.DAYS.between(LocalDate.now(), lot.getExpirationDate()), result.get(0).getDaysToExpire());
        verify(lotDataMapper).createLotDtoFromLotEntity(lot);
    }

    @Test
    void checkSupplyStock() {
        when(lotRepository.sumTotalStockFromSupply(1L)).thenReturn(new BigDecimal("50"));
        SupplyEntity supply = MovementTestHelper.SUPPLY_1;
        when(supplyRepository.findById(1L)).thenReturn(Optional.of(supply));

        Boolean result = lotService.checkSupplyStock(1L, 1L, new BigDecimal("30"));

        assertTrue(result);
    }

    private List<SupplyLotEntity> copyLots() {
        return SupplyLotHelper.LOTS.stream()
                .map(lot -> SupplyLotEntity.builder()
                        .id(lot.getId())
                        .entry(lot.getEntry())
                        .initialQuantity(lot.getInitialQuantity())
                        .currentQuantity(lot.getCurrentQuantity())
                        .expirationDate(lot.getExpirationDate())
                        .notified(lot.isNotified())
                        .build())
                .toList();
    }
}