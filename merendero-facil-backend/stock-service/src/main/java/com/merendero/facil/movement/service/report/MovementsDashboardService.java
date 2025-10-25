package com.merendero.facil.movement.service.report;


import com.merendero.facil.movement.dto.report.MovementsDashboardDto;

import java.time.LocalDate;

public interface MovementsDashboardService {
    MovementsDashboardDto getSummaryMovements(Long supplyId, LocalDate startDate, LocalDate endDate, String groupBy);
}
