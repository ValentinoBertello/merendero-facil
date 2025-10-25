package com.merendero.facil.service.statistics;


import com.merendero.facil.dto.donation.statistics.DonationDashboardResponse;
import com.merendero.facil.dto.donation.statistics.DonationDateSummary;

import java.time.LocalDate;
import java.util.List;

public interface DonationStatisticsService {
    DonationDashboardResponse getDonationDashboard(LocalDate startDate, LocalDate endDate, String groupBy, Long merenderoId);

    List<DonationDateSummary> getDonationGroupsByTime(Long merenderoId, LocalDate startDate, LocalDate endDate, String groupBy);
}
