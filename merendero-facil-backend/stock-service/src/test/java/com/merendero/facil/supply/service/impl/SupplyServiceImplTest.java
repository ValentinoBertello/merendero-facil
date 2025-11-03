package com.merendero.facil.supply.service.impl;

import com.merendero.facil.helper.SupplyTestHelper;
import com.merendero.facil.stock.repository.SupplyLotRepository;
import com.merendero.facil.supply.dto.SupplyRequestDto;
import com.merendero.facil.supply.dto.SupplyResponseDto;
import com.merendero.facil.supply.entity.SupplyCategoryEntity;
import com.merendero.facil.supply.entity.SupplyEntity;
import com.merendero.facil.supply.mapper.SupplyDataMapper;
import com.merendero.facil.supply.repository.SupplyCategoryRepository;
import com.merendero.facil.supply.repository.SupplyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.merendero.facil.helper.SupplyTestHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplyServiceImplTest {
    @Mock
    private SupplyLotRepository lotRepository;
    @Mock
    private SupplyDataMapper supplyDataMapper;
    @Mock
    private SupplyRepository supplyRepository;
    @Mock
    private SupplyCategoryRepository categoryRepository;

    @InjectMocks
    private SupplyServiceImpl supplyService;

    @Test
    void saveSupply() {
        SupplyRequestDto request = SupplyRequestDto.builder()
                .name("Arroz")
                .supplyCategoryId(1L)
                .minQuantity(new BigDecimal("10"))
                .build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.ofNullable(CATEGORY_ALIMENTOS));
        when(supplyDataMapper.mapSupplyRequestToSupplyEntity(request, CATEGORY_ALIMENTOS, 1L))
                .thenReturn(SUPPLY_ARROZ);
        when(supplyRepository.save(SUPPLY_ARROZ)).thenReturn(SUPPLY_ARROZ);
        when(supplyDataMapper.mapSupplyEntityToSupplyResponse(SUPPLY_ARROZ)).thenReturn(new SupplyResponseDto());

        SupplyResponseDto supplyResponseDto = this.supplyService.saveSupply(1L, request);

        assertNotNull(supplyResponseDto);
        verify(categoryRepository).findById(1L);
        verify(supplyDataMapper).mapSupplyRequestToSupplyEntity(request, CATEGORY_ALIMENTOS, 1L);
        verify(supplyRepository).save(SUPPLY_ARROZ);
        verify(supplyDataMapper).mapSupplyEntityToSupplyResponse(SUPPLY_ARROZ);
    }

    @Test
    void getSuppliesFromMerendero() {
        List<SupplyEntity> supplies = List.of(SUPPLY_ARROZ, SUPPLY_FIDEOS);
        when(supplyRepository.findByMerenderoIdAndActiveTrue(1L)).thenReturn(supplies);
        when(supplyDataMapper.mapSupplyEntitiesToSupplyResponses(supplies))
                .thenReturn(List.of(new SupplyResponseDto(), new SupplyResponseDto()));

        List<SupplyResponseDto> result = supplyService.getSuppliesFromMerendero(1L);

        assertEquals(2, result.size());
        verify(supplyRepository).findByMerenderoIdAndActiveTrue(1L);
        verify(supplyDataMapper).mapSupplyEntitiesToSupplyResponses(supplies);
    }

    @Test
    void getSupplyCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(SupplyTestHelper.CATEGORY_ALIMENTOS));

        List<SupplyCategoryEntity> result = supplyService.getSupplyCategories();

        assertEquals(1, result.size());
        assertEquals("Alimentos", result.get(0).getName());
        verify(categoryRepository).findAll();
    }

    @Test
    void removeSupplyFromMerendero() {
        when(supplyRepository.findById(1L)).thenReturn(Optional.of(SupplyTestHelper.SUPPLY_ARROZ));

        Long removedId = supplyService.removeSupplyFromMerendero(1L, 1L);

        assertEquals(1L, removedId);
        assertFalse(SupplyTestHelper.SUPPLY_ARROZ.getActive());
        verify(supplyRepository).save(SupplyTestHelper.SUPPLY_ARROZ);
        verify(lotRepository).deleteByEntry_Supply_Id(1L);
    }
}