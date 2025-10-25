package com.merendero.facil.expense.service.impl;

import com.merendero.facil.common.clients.dto.DonationDateSummary;
import com.merendero.facil.expense.dto.statistics.TimeGroupedExpenseData;
import com.merendero.facil.expense.entity.ExpenseEntity;
import com.merendero.facil.expense.service.DataGroupingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class DataGroupingServiceImpl implements DataGroupingService {
    private static final Locale LOCALE_AR = new Locale("es", "AR");

    /**
     * Método principal de agrupación que combina y organiza gastos y donaciones por períodos de tiempo
     */
    @Override
    public List<TimeGroupedExpenseData> groupDataByPeriod(List<ExpenseEntity> expenses,
                                                          List<DonationDateSummary> donations,
                                                          String groupBy) {
        Map<LocalDate, TimeGroupedExpenseData> map = new HashMap<>();
        // ---- Agregar gastos al map ----
        this.processExpenses(expenses, map, groupBy);
        // ---- Agregar donaciones al map ----
        this.processDonations(donations, map, groupBy);
        // Se convierte el map a Lista
        List<TimeGroupedExpenseData> groups = new ArrayList<>(map.values());
        groups.sort(Comparator.comparing(TimeGroupedExpenseData::getDate));
        return groups;
    }

    /**
     * Procesa cada gasto y lo acumula en el grupo de fecha correspondiente.
     **/
    private void processExpenses(List<ExpenseEntity> expenses, Map<LocalDate, TimeGroupedExpenseData> map,
                                  String groupBy) {
        for (ExpenseEntity e : expenses) {
            LocalDate expenseDate = LocalDate.from(e.getExpenseDate());
            LocalDate bucketDate = this.getBucket(expenseDate, groupBy);

            TimeGroupedExpenseData timeGroupedData;
            if (map.containsKey(bucketDate)) {
                timeGroupedData = map.get(bucketDate);
            } else {
                timeGroupedData = TimeGroupedExpenseData.builder()
                        .donationAmount(BigDecimal.ZERO)
                        .expenseAmount(BigDecimal.ZERO)
                        .date(bucketDate)
                        .label(this.getBucketLabel(bucketDate, groupBy))
                        .build();
                map.put(bucketDate, timeGroupedData);
            }
            timeGroupedData.setExpenseAmount(timeGroupedData.getExpenseAmount().add(e.getAmount()));
        }
    }

    /**
     * Procesa las donaciones que YA vienen agrupadas desde el otro microservicio
     * Solo necesita asignarlas a las fechas correspondientes en el mapa
     */
    private void processDonations(List<DonationDateSummary> donations, Map<LocalDate, TimeGroupedExpenseData> map,
                                  String groupBy) {
        for (DonationDateSummary donation : donations) {
            // Las donaciones ya vienen con la fecha agrupada correctamente
            LocalDate bucketDate = donation.getDate();

            TimeGroupedExpenseData groupedData = map.computeIfAbsent(bucketDate,
                    date -> TimeGroupedExpenseData.builder()
                            .donationAmount(BigDecimal.ZERO)
                            .expenseAmount(BigDecimal.ZERO)
                            .date(date)
                            .label(this.getBucketLabel(bucketDate, groupBy))
                            .build());

            groupedData.setDonationAmount(groupedData.getDonationAmount().add(donation.getAmountDonated()));
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