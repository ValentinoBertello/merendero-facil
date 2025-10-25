package com.merendero.facil.service;

import com.merendero.facil.dto.donation.DonationRequestDto;
import com.merendero.facil.dto.merendero.MerenderoResponseDto;
import com.merendero.facil.dto.mp.PayRequestDto;

import java.math.BigDecimal;
import java.util.Map;

public interface MercadoPagoPaymentService {
    String createPreference(PayRequestDto payRequestDto, MerenderoResponseDto merendero);

    DonationRequestDto processPaymentNotification(String donorEmail, BigDecimal amount, Map<String, Object> requestBody);
}
