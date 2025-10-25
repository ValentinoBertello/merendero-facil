package com.merendero.facil.movement.service.entry;


import com.merendero.facil.movement.dto.entry.EntryRequestDto;
import com.merendero.facil.movement.dto.entry.EntryResponseDto;
import com.merendero.facil.movement.entity.EntryEntity;

import java.util.List;

public interface EntryService {

    List<EntryResponseDto> getEntriesFromMerendero(Long merenderoId);
    EntryResponseDto createEntry(EntryRequestDto entryRequestDto);
    EntryEntity createEntryEntity(EntryRequestDto entryRequestDto);
}
