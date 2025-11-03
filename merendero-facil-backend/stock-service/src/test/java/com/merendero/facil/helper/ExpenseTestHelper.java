package com.merendero.facil.helper;

import com.merendero.facil.common.clients.dto.DonationDateSummary;
import com.merendero.facil.expense.entity.ExpenseEntity;
import com.merendero.facil.expense.entity.ExpenseTypeEntity;
import com.merendero.facil.movement.dto.entry.EntryType;
import com.merendero.facil.movement.entity.EntryEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.merendero.facil.helper.SupplyTestHelper.*;


public class ExpenseTestHelper {
    public static final ExpenseTypeEntity TYPE_COMPRA_INSUMOS = ExpenseTypeEntity.builder()
            .id(1L)
            .description("Compra de Insumos")
            .build();

    public static final ExpenseTypeEntity TYPE_LUZ_GAS = ExpenseTypeEntity.builder()
            .id(2L)
            .description("Luz y Gas")
            .build();

    public static final ExpenseTypeEntity TYPE_OTROS = ExpenseTypeEntity.builder()
            .id(3L)
            .description("Otros")
            .build();

    // EntryEntity mocks
    public static final EntryEntity ENTRY_ARROZ = EntryEntity.builder()
            .id(1L)
            .supply(SUPPLY_ARROZ)
            .quantity(new BigDecimal("20"))
            .entryDate(LocalDateTime.of(2025, 1, 15, 10, 0))
            .entryType(EntryType.PURCHASE)
            .build();

    public static final EntryEntity ENTRY_LECHE = EntryEntity.builder()
            .id(2L)
            .supply(SUPPLY_LECHE)
            .quantity(new BigDecimal("10"))
            .entryDate(LocalDateTime.of(2025, 1, 20, 14, 30))
            .entryType(EntryType.PURCHASE)
            .build();

    public static final EntryEntity ENTRY_FIDEOS = EntryEntity.builder()
            .id(3L)
            .supply(SUPPLY_FIDEOS)
            .quantity(new BigDecimal("15"))
            .entryDate(LocalDateTime.of(2025, 1, 25, 16, 0))
            .entryType(EntryType.PURCHASE)
            .build();

    // ExpenseEntity mocks
    public static final ExpenseEntity EXPENSE_1 = ExpenseEntity.builder()
            .id(1L)
            .merenderoId(1L)
            .amount(new BigDecimal("5000.00"))
            .entry(ENTRY_ARROZ)
            .type(TYPE_COMPRA_INSUMOS)
            .expenseDate(LocalDateTime.of(2025, 1, 15, 10, 0)) // 15 de enero
            .build();

    public static final ExpenseEntity EXPENSE_2 = ExpenseEntity.builder()
            .id(2L)
            .merenderoId(1L)
            .amount(new BigDecimal("2500.50"))
            .entry(ENTRY_LECHE)
            .type(TYPE_COMPRA_INSUMOS)
            .expenseDate(LocalDateTime.of(2025, 1, 20, 14, 30)) // 20 de enero
            .build();

    public static final ExpenseEntity EXPENSE_3 = ExpenseEntity.builder()
            .id(3L)
            .merenderoId(1L)
            .amount(new BigDecimal("3000.00"))
            .entry(ENTRY_FIDEOS)
            .type(TYPE_COMPRA_INSUMOS)
            .expenseDate(LocalDateTime.of(2025, 1, 25, 16, 0)) // 25 de enero
            .build();

    public static final ExpenseEntity EXPENSE_4 = ExpenseEntity.builder()
            .id(4L)
            .merenderoId(1L)
            .amount(new BigDecimal("1500.00"))
            .entry(null)
            .type(TYPE_LUZ_GAS)
            .expenseDate(LocalDateTime.of(2025, 1, 10, 8, 0)) // 10 de enero
            .build();

    public static final ExpenseEntity EXPENSE_5 = ExpenseEntity.builder()
            .id(5L)
            .merenderoId(1L)
            .amount(new BigDecimal("800.75"))
            .entry(null)
            .type(TYPE_OTROS)
            .expenseDate(LocalDateTime.of(2025, 1, 5, 11, 30)) // 5 de enero
            .build();

    public static final ExpenseEntity EXPENSE_6 = ExpenseEntity.builder()
            .id(6L)
            .merenderoId(1L)
            .amount(new BigDecimal("2500.00"))
            .entry(ENTRY_ARROZ)
            .type(TYPE_COMPRA_INSUMOS)
            .expenseDate(LocalDateTime.of(2025, 1, 15, 10, 0)) // 15 de enero
            .build();

    // DonationDateSummary mocks
    public static final DonationDateSummary DONATION_1 = DonationDateSummary.builder()
            .date(LocalDate.of(2025, 1, 15)) // 15 de enero
            .amountDonated(new BigDecimal("10000.00"))
            .build();

    public static final DonationDateSummary DONATION_2 = DonationDateSummary.builder()
            .date(LocalDate.of(2025, 1, 20)) // 20 de enero
            .amountDonated(new BigDecimal("5000.00"))
            .build();

    public static final DonationDateSummary DONATION_3 = DonationDateSummary.builder()
            .date(LocalDate.of(2025, 1, 25)) // 25 de enero
            .amountDonated(new BigDecimal("3000.00"))
            .build();

    public static final DonationDateSummary DONATION_4 = DonationDateSummary.builder()
            .date(LocalDate.of(2025, 1, 15)) // 15 de enero
            .amountDonated(new BigDecimal("2500.00"))
            .build();

    public static final DonationDateSummary DONATION_MONTHLY = DonationDateSummary.builder()
            .date(LocalDate.of(2025, 1, 1))
            .amountDonated(new BigDecimal("20500.00"))
            .build();

    public static final DonationDateSummary DONATION_MONTHLY_2 = DonationDateSummary.builder()
            .date(LocalDate.of(2025, 2, 1))
            .amountDonated(new BigDecimal("51200.00"))
            .build();

    public static final DonationDateSummary DONATION_WEEKLY = DonationDateSummary.builder()
            .date(LocalDate.of(2025, 1, 13)) // Lunes 13 de enero
            .amountDonated(new BigDecimal("12500.00"))
            .build();

    public static final DonationDateSummary DONATION_WEEKLY_2 = DonationDateSummary.builder()
            .date(LocalDate.of(2025, 1, 20)) // Lunes 20 de enero
            .amountDonated(new BigDecimal("8000.00"))
            .build();

    public static final List<ExpenseEntity> ALL_EXPENSES = List.of(
            EXPENSE_1, EXPENSE_2, EXPENSE_3, EXPENSE_4, EXPENSE_5, EXPENSE_6
    );

    public static final List<DonationDateSummary> ALL_DONATIONS = List.of(
            DONATION_1, DONATION_2, DONATION_3, DONATION_4
    );
}