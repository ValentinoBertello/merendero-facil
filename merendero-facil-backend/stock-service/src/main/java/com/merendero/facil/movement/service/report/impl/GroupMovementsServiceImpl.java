package com.merendero.facil.movement.service.report.impl;

import com.merendero.facil.movement.dto.entry.EntryType;
import com.merendero.facil.movement.dto.report.GroupMovementsDto;
import com.merendero.facil.movement.entity.EntryEntity;
import com.merendero.facil.movement.entity.OutputEntity;
import com.merendero.facil.movement.service.report.GroupMovementsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class GroupMovementsServiceImpl implements GroupMovementsService {
    private static final Locale LOCALE_AR = new Locale("es", "AR");

    /**
     * Obtiene lista de `GroupMovementsDto` con datos de entradas y salidas entre dos fechas.
     * Dependiendo del groupBy, puede venir un `GroupMovementsDto` por día, por semana o por mes.
     **/
    @Override
    @Transactional(readOnly = true)
    public List<GroupMovementsDto> getGroupMovements(List<EntryEntity> entries,
                                                     List<OutputEntity> outputs,
                                                     String groupBy) {
        // Este mapa almacenará un GroupMovementsDto por cada "bucket" de fecha
        Map<LocalDate, GroupMovementsDto> dateWithDtoMap = new HashMap<>();

        // ---- procesar entradas ----
        this.processEntries(entries, dateWithDtoMap, groupBy);

        // ---- procesar salidas ----
        this.processOutputs(outputs, dateWithDtoMap, groupBy);

        // convertir a lista y ordenar por fecha ascendente
        List<GroupMovementsDto> groups = new ArrayList<>(dateWithDtoMap.values());
        groups.sort(Comparator.comparing(GroupMovementsDto::getDate));
        return groups;
    }

    /**
     * Procesa la lista de entradas y las acumula en el mapa dateWithDtoMap
     * según el bucket calculado por getBucket()
     **/
    private void processEntries(List<EntryEntity> entryEntities,
                                Map<LocalDate, GroupMovementsDto> dateWithDtoMap,
                                String groupBy) {
        for (EntryEntity e : entryEntities) {
            LocalDate entryDate = LocalDate.from(e.getEntryDate());
            // Obtenemos una de las fechas en la cual se acumularan datos
            LocalDate bucketDate = this.getBucket(entryDate, groupBy);

            GroupMovementsDto dto;
            if (dateWithDtoMap.containsKey(bucketDate)) {
                dto = dateWithDtoMap.get(bucketDate);
            } else {
                dto = createEmptyDto(bucketDate, groupBy);
                dateWithDtoMap.put(bucketDate, dto);
            }

            if (e.getEntryType() == EntryType.DONATION) {
                dto.setEntryDonationQty(dto.getEntryDonationQty().add(e.getQuantity()));
            } else {
                dto.setEntryPurchaseQty(dto.getEntryPurchaseQty().add(e.getQuantity()));
            }
        }
    }

    /**
     * Procesa la lista de salidas y las acumula en el mapa dateWithDtoMap
     * según el bucket calculado por getBucket(...).
     */
    private void processOutputs(List<OutputEntity> outputs,
                                Map<LocalDate, GroupMovementsDto> dateWithDtoMap,
                                String groupBy) {
        for (OutputEntity o : outputs) {
            LocalDate outputDate = LocalDate.from(o.getOutputDate());
            LocalDate bucketDate = getBucket(outputDate, groupBy);

            GroupMovementsDto dto;
            if (dateWithDtoMap.containsKey(bucketDate)) {
                dto = dateWithDtoMap.get(bucketDate);
            } else {
                dto = createEmptyDto(bucketDate, groupBy);
                dateWithDtoMap.put(bucketDate, dto);
            }

            dto.setOutputQty(dto.getOutputQty().add(o.getQuantity()));
        }
    }

    /**
     * Devuelve la fecha agrupamiento según el groupBy (day/ week/ month).
     *  - "day"  → devuelve la misma fecha (ej: 2025-06-05 -> 2025-06-05)
     *  - "week" → devuelve el lunes de esa semana (o el mismo día si ya es lunes)
     *  - "month"-> devuelve el primer día del mes
     **/
    private LocalDate getBucket(LocalDate date, String groupBy){
        if ("day".equals(groupBy)) {
            return date;
        } else if ("month".equals(groupBy)) {
            return date.withDayOfMonth(1);
        } else {
            return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
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

    /**
     * Crea e inicializa un GroupMovementsDto con valores en cero y la fecha indicada.
     */
    private GroupMovementsDto createEmptyDto(LocalDate date, String groupBy) {
        GroupMovementsDto dto = new GroupMovementsDto();
        dto.setDate(date);
        dto.setEntryDonationQty(BigDecimal.ZERO);
        dto.setEntryPurchaseQty(BigDecimal.ZERO);
        dto.setOutputQty(BigDecimal.ZERO);
        dto.setLabel(this.getBucketLabel(date, groupBy));
        return dto;
    }
}