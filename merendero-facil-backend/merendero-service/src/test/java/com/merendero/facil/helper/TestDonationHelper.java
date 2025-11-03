package com.merendero.facil.helper;

import com.merendero.facil.dto.donation.DonationRequestDto;
import com.merendero.facil.entities.DonationEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.merendero.facil.helper.TestMerenderoHelper.MERENDERO_1;

public class TestDonationHelper {

    // ------- Donaciones (DonationEntity) -------
    public static final DonationEntity DONATION_1 = DonationEntity.builder()
            .id(1L)
            .userEmail("donante1@email.com")
            .donationDate(LocalDateTime.of(2024, 1, 15, 10, 30))// Lunes 15 de Enero
            .netAmount(new BigDecimal("1000.00"))
            .grossAmount(new BigDecimal("1000.00"))
            .mpFee(new BigDecimal("50.00"))
            .paymentId("payment_001")
            .merendero(MERENDERO_1)
            .build();

    public static final DonationEntity DONATION_2 = DonationEntity.builder()
            .id(2L)
            .userEmail("donante2@email.com")
            .donationDate(LocalDateTime.of(2024, 1, 15, 14, 20))// Lunes 15 de Enero
            .netAmount(new BigDecimal("1500.00"))
            .grossAmount(new BigDecimal("1500.00"))
            .mpFee(new BigDecimal("75.00"))
            .paymentId("payment_002")
            .build();

    public static final DonationEntity DONATION_3 = DonationEntity.builder()
            .id(3L)
            .userEmail("donante1@email.com") // Mismo donante que DONATION_1
            .donationDate(LocalDateTime.of(2024, 1, 16, 9, 15))// Martes 16 de Enero
            .netAmount(new BigDecimal("2000.00"))
            .grossAmount(new BigDecimal("2000.00"))
            .mpFee(new BigDecimal("100.00"))
            .paymentId("payment_003")
            .build();

    public static final DonationEntity DONATION_4 = DonationEntity.builder()
            .id(4L)
            .userEmail("donante3@email.com")
            .donationDate(LocalDateTime.of(2024, 1, 20, 16, 45))// Sábado 20 de Enero
            .netAmount(new BigDecimal("500.00"))
            .grossAmount(new BigDecimal("500.00"))
            .mpFee(new BigDecimal("25.00"))
            .paymentId("payment_004")
            .build();

    public static final DonationEntity DONATION_5 = DonationEntity.builder()
            .id(5L)
            .userEmail("donante4@email.com")
            .donationDate(LocalDateTime.of(2024, 2, 1, 11, 0))// Jueves 1 de Febrero
            .netAmount(new BigDecimal("3000.00"))
            .grossAmount(new BigDecimal("3000.00"))
            .mpFee(new BigDecimal("150.00"))
            .paymentId("payment_005")
            .build();

    public static final DonationEntity DONATION_6 = DonationEntity.builder()
            .id(6L)
            .userEmail("donante5@email.com")
            .donationDate(LocalDateTime.of(2024, 2, 5, 13, 30))// Lunes 5 de Febrero
            .netAmount(new BigDecimal("750.00"))
            .grossAmount(new BigDecimal("750.00"))
            .mpFee(new BigDecimal("37.50"))
            .paymentId("payment_006")
            .build();

    public static final DonationEntity DONATION_7 = DonationEntity.builder()
            .id(7L)
            .userEmail("donante6@email.com")
            .donationDate(LocalDateTime.of(2024, 1, 8, 8, 0))// Lunes 8 de Enero
            .netAmount(new BigDecimal("1200.00"))
            .grossAmount(new BigDecimal("1200.00"))
            .mpFee(new BigDecimal("60.00"))
            .paymentId("payment_007")
            .build();

    public static final DonationEntity DONATION_8 = DonationEntity.builder()
            .id(8L)
            .userEmail("donante7@email.com")
            .donationDate(LocalDateTime.of(2024, 1, 10, 17, 20))// Miércoles 10 de Enero
            .netAmount(new BigDecimal("800.00"))
            .grossAmount(new BigDecimal("800.00"))
            .mpFee(new BigDecimal("40.00"))
            .paymentId("payment_008")
            .build();

    public static final DonationEntity DONATION_9 = DonationEntity.builder()
            .id(9L)
            .userEmail("donante8@email.com")
            .donationDate(LocalDateTime.of(2024, 3, 15, 12, 0))// Viernes 15 de Marzo
            .netAmount(new BigDecimal("2500.00"))
            .grossAmount(new BigDecimal("2500.00"))
            .mpFee(new BigDecimal("125.00"))
            .paymentId("payment_009")
            .build();

    public static final DonationEntity DONATION_10 = DonationEntity.builder()
            .id(10L)
            .userEmail("donante1@email.com") // Mismo donante que DONATION_1 y DONATION_3
            .donationDate(LocalDateTime.of(2024, 1, 15, 19, 45))// Lunes 15 de Enero
            .netAmount(new BigDecimal("1800.00"))
            .grossAmount(new BigDecimal("1800.00"))
            .mpFee(new BigDecimal("90.00"))
            .paymentId("payment_010")
            .build();

    public static final List<DonationEntity> ALL_DONATIONS = List.of(
            DONATION_1, DONATION_2, DONATION_3, DONATION_4, DONATION_5, DONATION_6, DONATION_7, DONATION_8, DONATION_9,
            DONATION_10
    );

    public static final DonationRequestDto DONATION_REQUEST_1 = DonationRequestDto.builder()
            .userEmail("manager1@merendero.com")
            .merenderoId(1L)
            .donationDate(LocalDateTime.of(2025, 11, 2, 15, 30))
            .paymentId("MP123456789")
            .grossAmount(new BigDecimal("1000.00"))
            .mpFee(new BigDecimal("50.00"))
            .netAmount(new BigDecimal("950.00"))
            .build();
}