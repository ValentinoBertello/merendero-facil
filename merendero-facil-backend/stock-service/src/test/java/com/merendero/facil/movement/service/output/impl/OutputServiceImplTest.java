package com.merendero.facil.movement.service.output.impl;

import com.merendero.facil.movement.dto.output.OutputRequestDto;
import com.merendero.facil.movement.dto.output.OutputResponseDto;
import com.merendero.facil.movement.mapper.OutputDataMapper;
import com.merendero.facil.movement.repository.OutputRepository;
import com.merendero.facil.movement.service.output.EmailService;
import com.merendero.facil.stock.service.LotService;
import com.merendero.facil.supply.entity.SupplyEntity;
import com.merendero.facil.supply.repository.SupplyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.merendero.facil.helper.MovementTestHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutputServiceImplTest {
    @Mock
    private EmailService emailService;
    @Mock
    private SupplyRepository supplyRepository;
    @Mock
    private OutputDataMapper outputDataMapper;
    @Mock
    private OutputRepository outputRepository;
    @Mock
    private LotService lotService;

    @InjectMocks
    private OutputServiceImpl outputService;

    @Test
    void createOutput() {
        // Mock del objeto "request"
        OutputRequestDto req = mock(OutputRequestDto.class);
        when(req.getSupplyId()).thenReturn(SUPPLY_1.getId());

        when(supplyRepository.findById(SUPPLY_1.getId()))
                .thenReturn(Optional.of(SUPPLY_1));
        when(outputDataMapper.mapOutputRequestToOutputEntity(any(), eq(SUPPLY_1)))
                .thenReturn(OUTPUT_1);
        when(outputRepository.save(OUTPUT_1)).thenReturn(OUTPUT_1);

        when(lotService.getTotalStockBySupply(SUPPLY_1.getMerenderoId(), SUPPLY_1.getId()))
                .thenReturn(new BigDecimal("5"));

        OutputResponseDto dto = mock(OutputResponseDto.class);
        when(outputDataMapper.mapOutputEntityToOutputResponse(OUTPUT_1))
                .thenReturn(dto);

        OutputResponseDto result = this.outputService.createOutput(req);

        assertSame(dto, result);

        // Verificación del branch de stock bajo: email enviado y supply guardado con fecha de hoy
        verify(lotService).getTotalStockBySupply(eq(SUPPLY_1.getMerenderoId()), eq(SUPPLY_1.getId()));
        verify(emailService).sendLowStockEmail(any(SupplyEntity.class));

        // Verifica que el método haya guardado el supply con la fecha de alerta actualizada
        ArgumentCaptor<SupplyEntity> supplyCaptor = ArgumentCaptor.forClass(SupplyEntity.class);
        verify(supplyRepository).save(supplyCaptor.capture());
        assertEquals(LocalDate.now(), supplyCaptor.getValue().getLastAlertDate());
    }

    @Test
    void getOutputsFromMerendero() {
        when(outputRepository.findByMerenderoId(1L))
                .thenReturn(List.of(OUTPUT_1));
        when(outputDataMapper.mapOutputEntitiesToOutputResponses(List.of(OUTPUT_1)))
                .thenReturn(List.of(mock(OutputResponseDto.class)));

        List<OutputResponseDto> result = outputService.getOutputsFromMerendero(1L);

        assertEquals(1, result.size());
        verify(outputRepository).findByMerenderoId(1L);
        verify(outputDataMapper).mapOutputEntitiesToOutputResponses(List.of(OUTPUT_1));
    }
}