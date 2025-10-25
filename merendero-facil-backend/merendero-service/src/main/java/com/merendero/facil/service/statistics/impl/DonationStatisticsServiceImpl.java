package com.merendero.facil.service.statistics.impl;

import com.merendero.facil.dto.donation.statistics.*;
import com.merendero.facil.entities.DonationEntity;
import com.merendero.facil.repository.DonationRepository;
import com.merendero.facil.service.statistics.DonationGroupingService;
import com.merendero.facil.service.statistics.DonationStatisticsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DonationStatisticsServiceImpl implements DonationStatisticsService {

    private final DonationRepository donationRepository;
    private final DonationGroupingService donationGroupingService;

    public DonationStatisticsServiceImpl(DonationRepository donationRepository, DonationGroupingService donationGroupingService) {
        this.donationRepository = donationRepository;
        this.donationGroupingService = donationGroupingService;
    }

    /**
     * Construye y retorna el dashboard completo de estadísticas de donaciones con todos los datos,
     * comaprativas entre períodos y análisis de donantes para un merendero específico.
     **/
    @Override
    @Transactional(readOnly = true)
    public DonationDashboardResponse getDonationDashboard(LocalDate startDate, LocalDate endDate, String groupBy,
                                                          Long merenderoId) {
        // Obtiene todas las donaciones entre las fechas recibidas
        List<DonationEntity> donationEntities = this.getDonationsInDateRange(startDate, endDate, merenderoId);

        if (donationEntities.isEmpty()) {
            return new DonationDashboardResponse();
        }

        // Obtiene las fechas `startDate` y `endDate´ del periodo anterior
        PeriodDates previousPeriod = this.calculatePreviousPeriodDates(startDate, endDate);

        // Obtiene las donaciones recibidas en el periodo anterior
        List<DonationEntity> donationEntitiesPreviousPeriod =
                this.getDonationsInDateRange(previousPeriod.getStartDate(), previousPeriod.getEndDate(), merenderoId);

        // Obtiene los resúmenes de donaciones agrupadas por fecha según el criterio (día, semana o mes)
        List<DonationDateSummary> donationDateSummaries =
                this.donationGroupingService.groupDonationsByPeriod(donationEntities, groupBy);

        // Obtiene las estadísticas del período actual y anterior
        PeriodStats currentPeriodStats = this.getPeriodStats(donationEntities, startDate, endDate);
        PeriodStats previousPeriodStats = this.getPeriodStats(
                donationEntitiesPreviousPeriod,
                previousPeriod.getStartDate(),
                previousPeriod.getEndDate());

        // Obtiene entre el periodo actual y el anterior
        ComparisonStats comparisonStats = this.getComparisonStats(currentPeriodStats, previousPeriodStats);

        // Obtiene el analisis de donantes (tipos y top donantes)
        DonorAnalysis donorAnalysis = this.getDonorAnalysis(donationEntities, startDate, merenderoId);

        // Construye y retorna la respuesta del dashboard con todos los datos calculados
        return this.buildDashboardResponse(currentPeriodStats, previousPeriodStats, comparisonStats,
                donationDateSummaries, donorAnalysis);
    }

    @Override
    public List<DonationDateSummary> getDonationGroupsByTime(Long merenderoId, LocalDate startDate, LocalDate endDate,
                                                             String groupBy) {
        List<DonationEntity> donations = this.getDonationsInDateRange(startDate, endDate, merenderoId);
        return donationGroupingService.groupDonationsByPeriod(donations, groupBy);
    }

    /**
     * Obtiene las donaciones de un merendero en un rango de fechas (convertido a LocalDateTime automáticamente)
     */
    private List<DonationEntity> getDonationsInDateRange(LocalDate startDate, LocalDate endDate, Long merenderoId) {
        return this.donationRepository.findByDatesAndMerendero(
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX),
                merenderoId
        );
    }

    /**
     * Construye la respuesta del dashboard con todos los componentes calculados
     */
    private DonationDashboardResponse buildDashboardResponse(PeriodStats currentPeriodStats,
                                                             PeriodStats previousPeriodStats,
                                                             ComparisonStats comparisonStats,
                                                             List<DonationDateSummary> donationDateSummaries,
                                                             DonorAnalysis donorAnalysis) {
        return DonationDashboardResponse.builder()
                .currentPeriod(currentPeriodStats)
                .previousPeriod(previousPeriodStats)
                .comparisonStats(comparisonStats)
                .donationDateSummaries(donationDateSummaries)
                .donorAnalysis(donorAnalysis)
                .build();
    }

    /**
     * Calcula el periodo anterior basado en la diferencia entre startDate y endDate
     **/
    private PeriodDates calculatePreviousPeriodDates(LocalDate startDate, LocalDate endDate) {
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1; // +1 para incluir ambos extremos

        LocalDate previousStartDate = startDate.minusDays(daysBetween);
        LocalDate previousEndDate = endDate.minusDays(daysBetween);

        return new PeriodDates(previousStartDate, previousEndDate);
    }

    /**
     * Construye el análisis completo de donantes con tipos y el top de mayores donadores.
     **/
    private DonorAnalysis getDonorAnalysis(List<DonationEntity> currentPeriodDonations, LocalDate periodStartDate,
                                           Long merenderoId) {
        DonorTypeAnalysis donorTypeAnalysis = this.getDonorTypeQuantities(currentPeriodDonations, periodStartDate,
                merenderoId);
        List<TopDonor> topDonorsCurrentPeriod = this.getTopDonors(currentPeriodDonations);

        return DonorAnalysis.builder()
                .donorTypeAnalysis(donorTypeAnalysis)
                .topDonors(topDonorsCurrentPeriod)
                .build();
    }

    /**
     * Devuelve objeto `ComparisonStats` conteniendo las comparativas entre dos periodos.
     * Comparativas de los montos totales recibidos y la cantidad de donaciones recibidad.
     **/
    private ComparisonStats getComparisonStats(PeriodStats current, PeriodStats previous) {
        return ComparisonStats.builder()
                .amountDonatedChange(this.calculateChangeStats(
                        current.getTotalAmountDonated(),
                        previous.getTotalAmountDonated()))
                .donationCountChange(this.calculateChangeStats(
                        BigDecimal.valueOf(current.getDonationCount()),
                        BigDecimal.valueOf(previous.getDonationCount())
                ))
                .build();
    }

    /**
     * Construye las estadísticas del período basado en las donaciones y fechas proporcionadas
     */
    private PeriodStats getPeriodStats(List<DonationEntity> donationEntities, LocalDate startDate, LocalDate endDate) {
        return PeriodStats.builder()
                .totalAmountDonated(this.calculateTotalAmount(donationEntities))
                .donationCount(donationEntities.size())
                .averagePerDonation(this.calculateAveragePerDonation(donationEntities))
                .averagePerDay(this.calculateAveragePerDay(donationEntities, startDate, endDate))
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    /**
     * Calcula el top de donantes en el periodo, ordenado por monto total donado.
     */
    private List<TopDonor> getTopDonors(List<DonationEntity> currentPeriodDonations) {
        Map<String, BigDecimal> donorAmountMap = new HashMap<>();
        // Agrupa y suma montos por donante en el `donorAmountMap`
        for (DonationEntity d : currentPeriodDonations) {
            BigDecimal currentAmount = donorAmountMap.getOrDefault(d.getUserEmail(), BigDecimal.ZERO);
            donorAmountMap.put(d.getUserEmail(), currentAmount.add(d.getNetAmount()));
        }
        // Convierte el map a `List<TopDonor>`
        List<TopDonor> topDonors = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : donorAmountMap.entrySet()) {
            topDonors.add(TopDonor.builder()
                    .donorEmail(entry.getKey())
                    .amountDonated(entry.getValue())
                    .build());
        }
        topDonors.sort((donor1, donor2) -> donor2.getAmountDonated().compareTo(donor1.getAmountDonated()));
        this.recortTopDonors(topDonors);
        return topDonors;
    }

    /**
     * Calcula la distribución de donantes por tipo (nuevos vs recurrentes).
     */
    private DonorTypeAnalysis getDonorTypeQuantities(List<DonationEntity> currentPeriodDonations,
                                                     LocalDate periodStartDate, Long merenderoId) {
        // Obtiene donantes únicos del periodo actual
        Set<String> currentPeriodDonors = currentPeriodDonations.stream()
                .map(DonationEntity::getUserEmail)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Obtiene donantes que ya habían donado antes del periodo actual
        Set<String> previousDonors = this.donationRepository.findPreviousDonorEmails(periodStartDate.atStartOfDay(),
                merenderoId);

        // Obtiene la cantidad de donantes nuevos vs recurrentes
        int newDonorsCount = 0;
        int recurrentDonorsCount = 0;
        for (String donor : currentPeriodDonors) {
            if (previousDonors.contains(donor)) {
                recurrentDonorsCount ++;
            } else {
                newDonorsCount++;
            }
        }

        return DonorTypeAnalysis.builder()
                .newDonorsCount(newDonorsCount)
                .recurrentDonorsCount(recurrentDonorsCount)
                .build();
    }

    /**
     * Calcula el cambio absoluto y porcentual entre dos valores.
     **/
    private ChangeStats calculateChangeStats(BigDecimal currentValue, BigDecimal previousValue) {
        // Cambio absoluto: actual - anterior
        BigDecimal absoluteChange = currentValue.subtract(previousValue);

        // Cambio porcentual: ((actual - anterior) / anterior) * 100
        BigDecimal percentageChange;

        // Manejar división por cero
        if (previousValue.compareTo(BigDecimal.ZERO) == 0) {
            // Si no había valor anterior, cualquier valor actual es un aumento del 100%
            // Si currentValue también es 0, entonces no hay cambio (0%)
            if (currentValue.compareTo(BigDecimal.ZERO) > 0) {
                percentageChange = BigDecimal.valueOf(100);
            } else if (currentValue.compareTo(BigDecimal.ZERO) < 0) {
                percentageChange = BigDecimal.valueOf(-100);
            } else {
                percentageChange = BigDecimal.ZERO; // Ambos son cero, no hay cambio
            }
        } else {
            // Cálculo normal
            percentageChange = currentValue.subtract(previousValue)
                    .divide(previousValue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return ChangeStats.builder()
                .value(absoluteChange)
                .percentage(percentageChange)
                .build();
    }

    /**
     * Calcula el promedio diario de donaciones dividiendo el total por la cantidad de días del período
     */
    private BigDecimal calculateAveragePerDay(List<DonationEntity> donations, LocalDate start, LocalDate end) {
        long daysInPeriod = ChronoUnit.DAYS.between(start, end) + 1;
        if (daysInPeriod <= 0) return BigDecimal.ZERO;
        BigDecimal total = calculateTotalAmount(donations);
        return total.divide(BigDecimal.valueOf(daysInPeriod), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula el promedio por donación dividiendo el monto total entre la cantidad de donaciones
     */
    private BigDecimal calculateAveragePerDonation(List<DonationEntity> donations) {
        if (donations == null || donations.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = this.calculateTotalAmount(donations);
        return total.divide(BigDecimal.valueOf(donations.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula el monto total donado sumando todos los netAmount de las donaciones
     */
    private BigDecimal calculateTotalAmount(List<DonationEntity> donations) {
        return donations.stream()
                .map(DonationEntity::getNetAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Recorta la lista de top donantes para mantener solo los 5 con mayor monto donado
     */
    private void recortTopDonors(List<TopDonor> topDonors) {
        if (topDonors.size() > 5) {
            topDonors.subList(5, topDonors.size()).clear();
        }
    }
}