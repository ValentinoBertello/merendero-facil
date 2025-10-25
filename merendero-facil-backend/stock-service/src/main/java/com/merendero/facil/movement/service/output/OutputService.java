package com.merendero.facil.movement.service.output;


import com.merendero.facil.movement.dto.output.OutputRequestDto;
import com.merendero.facil.movement.dto.output.OutputResponseDto;

import java.util.List;

public interface OutputService {
    OutputResponseDto createOutput(OutputRequestDto outputRequestDto);

    List<OutputResponseDto> getOutputsFromMerendero(Long merenderoId);
}
