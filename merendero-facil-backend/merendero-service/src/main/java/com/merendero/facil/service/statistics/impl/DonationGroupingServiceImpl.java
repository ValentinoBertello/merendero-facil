package com.merendero.facil.service.statistics.impl;

import com.merendero.facil.dto.donation.statistics.DonationDateSummary;
import com.merendero.facil.entities.DonationEntity;
import com.merendero.facil.service.statistics.DonationGroupingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class DonationGroupingServiceImpl implements DonationGroupingService {
    private static final Locale LOCALE_AR = new Locale("es", "AR");

    /**
     * Agrupa las donaciones por periodo según el periodo especificado (día, semana o mes)
     **/
    @Override
    @Transactional(readOnly = true)
    public List<DonationDateSummary> groupDonationsByPeriod(List<DonationEntity> donationEntities, String groupBy) {
        Map<LocalDate, DonationDateSummary> map = new HashMap<>();
        // ---- Agregar donaciones al map ----
        this.processDonations(donationEntities, map, groupBy);
        // Se convierte el map a Lista
        List<DonationDateSummary> groups = new ArrayList<>(map.values());
        groups.sort(Comparator.comparing(DonationDateSummary::getDate));
        return groups;
    }

    /**
     * Procesa cada donación y la acumula en el grupo de fecha correspondiente.
     **/
    private void processDonations(List<DonationEntity> donationEntities, Map<LocalDate, DonationDateSummary> map,
                                  String groupBy) {
        for (DonationEntity d : donationEntities) {
            LocalDate donationDate = LocalDate.from(d.getDonationDate());
            LocalDate bucketDate = this.getBucket(donationDate, groupBy);

            DonationDateSummary donationDateSummary;
            if (map.containsKey(bucketDate)) {
                donationDateSummary = map.get(bucketDate);
            } else {
                donationDateSummary = DonationDateSummary.builder()
                        .amountDonated(BigDecimal.ZERO)
                        .date(bucketDate)
                        .label(this.getBucketLabel(bucketDate, groupBy))
                        .build();
                map.put(bucketDate, donationDateSummary);
            }
            donationDateSummary.setAmountDonated(donationDateSummary.getAmountDonated().add(d.getNetAmount()));
        }
    }

    /**
     * Determina la fecha de agrupación según el tipo de período especificado
     */
    private LocalDate getBucket(LocalDate donationDate, String groupBy) {
        if ("day".equals(groupBy)) {
            return donationDate;
        } else if ("month".equals(groupBy)) {
            return donationDate.withDayOfMonth(1);
        } else {
            return donationDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }
    }

    /**
     * Genera el texto de etiqueta para la fecha agrupada según el período (día, semana o mes).
     */
    private String getBucketLabel(LocalDate date, String groupBy) {
        if ("day".equalsIgnoreCase(groupBy)) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM", LOCALE_AR);
            return date.format(fmt);
        } else if ("month".equalsIgnoreCase(groupBy)) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM yyyy", LOCALE_AR);
            return date.format(fmt);
        } else { // week: muestra rango "dd MMM - dd MMM"
            LocalDate endWeek = date.plusDays(6);
            DateTimeFormatter fmtFirst = DateTimeFormatter.ofPattern("d", LOCALE_AR);
            DateTimeFormatter fmtSecond = DateTimeFormatter.ofPattern("d MMM", LOCALE_AR);
            return date.format(fmtFirst) + " - " + endWeek.format(fmtSecond);
        }
    }
}