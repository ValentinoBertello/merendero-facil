package com.merendero.facil.movement.service.output.impl;

import com.merendero.facil.movement.dto.output.OutputRequestDto;
import com.merendero.facil.movement.dto.output.OutputResponseDto;
import com.merendero.facil.movement.entity.OutputEntity;
import com.merendero.facil.movement.mapper.OutputDataMapper;
import com.merendero.facil.movement.repository.OutputRepository;
import com.merendero.facil.movement.service.output.EmailService;
import com.merendero.facil.movement.service.output.OutputService;
import com.merendero.facil.stock.service.LotService;
import com.merendero.facil.supply.entity.SupplyEntity;
import com.merendero.facil.supply.repository.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Clase encargada de todoa la lógica de negocio de las salidas de insumos.
 **/
@Service
@RequiredArgsConstructor
public class OutputServiceImpl implements OutputService {

    private final EmailService emailService;
    private final SupplyRepository supplyRepository;
    private final OutputDataMapper outputDataMapper;
    private final OutputRepository outputRepository;
    private final LotService lotService;

    /**
     * Registra una salida de insumos.
     * En el proceso llama `deductFromLots` para que actualice el stock del insumo afectado.
     **/
    @Override
    @Transactional
    public OutputResponseDto createOutput(OutputRequestDto outputRequest) {
        SupplyEntity supply = this.findSupplyOrThrow(outputRequest.getSupplyId());
        OutputEntity outputEntity = this.outputDataMapper.mapOutputRequestToOutputEntity(outputRequest, supply);
        outputEntity = this.outputRepository.save(outputEntity);

        this.lotService.deductFromLots(outputEntity);
        this.handleStockBellowThreshold(supply);

        return this.outputDataMapper.mapOutputEntityToOutputResponse(outputEntity);
    }

    /**
     * Trae todas las salidas de insumos de un merendero en específico.
     **/
    @Override
    public List<OutputResponseDto> getOutputsFromMerendero(Long merenderoId) {
        List<OutputEntity> outputs = outputRepository.findByMerenderoId(merenderoId);
        return this.outputDataMapper.mapOutputEntitiesToOutputResponses(outputs);
    }

    /**
     * Busca un Supply por id y lanza IllegalArgumentException si no existe.
     */
    private SupplyEntity findSupplyOrThrow(Long supplyId) {
        return supplyRepository.findById(supplyId)
                .orElseThrow(() -> new IllegalArgumentException("Insumo no encontrado con id: " + supplyId));
    }

    /**
     * Verifica si el stock actual de un insumo está por debajo del umbral mínimo.
     * Si es así y no se ha enviado una alerta en el día, envía un email de stock bajo.
     */
    private void handleStockBellowThreshold(SupplyEntity supply) {
        if (this.lotService.getTotalStockBySupply(supply.getMerenderoId(),supply.getId())
                .compareTo(supply.getMinQuantity()) < 0) {
            if (!supply.getLastAlertDate().isEqual(LocalDate.now())) {
                this.emailService.sendLowStockEmail(supply);
                supply.setLastAlertDate(LocalDate.now());
                this.supplyRepository.save(supply);
            }
        }
    }
}
