package com.merendero.facil.movement.service.entry.impl;

import com.merendero.facil.expense.entity.ExpenseEntity;
import com.merendero.facil.expense.repository.ExpenseRepository;
import com.merendero.facil.helper.ExpenseTestHelper;
import com.merendero.facil.helper.MovementTestHelper;
import com.merendero.facil.movement.dto.entry.EntryRequestDto;
import com.merendero.facil.movement.dto.entry.EntryResponseDto;
import com.merendero.facil.movement.entity.EntryEntity;
import com.merendero.facil.movement.mapper.EntryDataMapper;
import com.merendero.facil.movement.repository.EntryRepository;
import com.merendero.facil.stock.service.LotService;
import com.merendero.facil.supply.entity.SupplyEntity;
import com.merendero.facil.supply.repository.SupplyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntryServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private LotService lotService;
    @Mock
    private SupplyRepository supplyRepository;
    @Mock
    private EntryRepository entryRepository;
    @Mock
    private EntryDataMapper entryDataMapper;

    @InjectMocks
    private EntryServiceImpl entryService;

    @Test
    void getEntriesFromMerendero() {
        Long merenderoId = 1L;
        List<EntryEntity> entries = MovementTestHelper.ALL_ENTRIES;

        ExpenseEntity expenseFor1 = ExpenseTestHelper.EXPENSE_1;
        ExpenseEntity expenseFor3 = ExpenseTestHelper.EXPENSE_3;

        when(entryRepository.findByMerenderoId(merenderoId)).thenReturn(entries);
        when(expenseRepository.findByEntryIdIn(List.of(1L,2L,3L,4L)))
                .thenReturn(List.of(expenseFor1, expenseFor3));

        EntryResponseDto dto1 = mock(EntryResponseDto.class);
        EntryResponseDto dto2 = mock(EntryResponseDto.class);
        EntryResponseDto dto3 = mock(EntryResponseDto.class);
        EntryResponseDto dto4 = mock(EntryResponseDto.class);

        when(entryDataMapper.mapEntryEntityToEntryResponse(MovementTestHelper.ENTRY_1)).thenReturn(dto1);
        when(entryDataMapper.mapEntryEntityToEntryResponse(MovementTestHelper.ENTRY_2)).thenReturn(dto2);
        when(entryDataMapper.mapEntryEntityToEntryResponse(MovementTestHelper.ENTRY_3)).thenReturn(dto3);
        when(entryDataMapper.mapEntryEntityToEntryResponse(MovementTestHelper.ENTRY_4)).thenReturn(dto4);

        List<EntryResponseDto> result = entryService.getEntriesFromMerendero(merenderoId);

        assertEquals(4, result.size());
        assertSame(dto1, result.get(0));
        assertSame(dto2, result.get(1));
    }

    @Test
    void createEntry() {
        EntryRequestDto req = mock(EntryRequestDto.class);
        EntryEntity created = MovementTestHelper.ENTRY_1;
        EntryResponseDto dto = mock(EntryResponseDto.class);

        EntryServiceImpl spySvc = spy(entryService);
        doReturn(created).when(spySvc).createEntryEntity(req);
        when(entryDataMapper.mapEntryEntityToEntryResponse(created)).thenReturn(dto);

        EntryResponseDto actual = spySvc.createEntry(req);

        assertSame(dto, actual);
        verify(entryDataMapper).mapEntryEntityToEntryResponse(created);
    }

    @Test
    void createEntryEntity() {
        EntryRequestDto req = mock(EntryRequestDto.class);
        LocalDate futureDate = LocalDate.now().plusDays(1);
        when(req.getExpirationDate()).thenReturn(futureDate);
        when(req.getSupplyId()).thenReturn(MovementTestHelper.SUPPLY_1.getId());

        SupplyEntity supply = MovementTestHelper.SUPPLY_1;
        when(supplyRepository.findById(supply.getId())).thenReturn(Optional.of(supply));

        EntryEntity mapped = EntryEntity.builder()
                .id(MovementTestHelper.ENTRY_1.getId())
                .supply(supply)
                .quantity(MovementTestHelper.ENTRY_1.getQuantity())
                .entryDate(MovementTestHelper.ENTRY_1.getEntryDate())
                .entryType(MovementTestHelper.ENTRY_1.getEntryType())
                .build();

        when(entryDataMapper.mapEntryRequestToEntryEntity(req, supply)).thenReturn(mapped);
        when(entryRepository.save(mapped)).thenReturn(mapped);

        EntryEntity saved = entryService.createEntryEntity(req);

        assertSame(mapped, saved);
    }
}