package com.merendero.facil.movement.service.report;


import com.merendero.facil.movement.dto.report.GroupMovementsDto;
import com.merendero.facil.movement.entity.EntryEntity;
import com.merendero.facil.movement.entity.OutputEntity;

import java.util.List;

public interface GroupMovementsService {

    List<GroupMovementsDto> getGroupMovements(List<EntryEntity> entries,
                                              List<OutputEntity> outputs,
                                              String groupBy);

}
