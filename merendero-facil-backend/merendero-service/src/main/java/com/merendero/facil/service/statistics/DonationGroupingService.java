package com.merendero.facil.service.statistics;


import com.merendero.facil.dto.donation.statistics.DonationDateSummary;
import com.merendero.facil.entities.DonationEntity;

import java.util.List;

public interface DonationGroupingService {
    List<DonationDateSummary> groupDonationsByPeriod(List<DonationEntity> donationEntities, String groupBy);
}
