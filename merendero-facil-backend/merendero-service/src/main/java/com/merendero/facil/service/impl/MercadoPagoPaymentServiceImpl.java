package com.merendero.facil.service.impl;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.payment.PaymentFeeDetail;
import com.mercadopago.resources.preference.Preference;
import com.merendero.facil.dto.donation.DonationRequestDto;
import com.merendero.facil.dto.merendero.MerenderoResponseDto;
import com.merendero.facil.dto.mp.PayRequestDto;
import com.merendero.facil.service.MercadoPagoPaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Servicio para la gestión de pagos a través de Mercado Pago.
 */
@Service
public class MercadoPagoPaymentServiceImpl implements MercadoPagoPaymentService {

    // Se utiliza una URL pública (un tunel),
    // Ya que Mercado Pago necesita acceso desde internet para webhooks
    private final String ngrokUrl;

    private final Logger logger = LoggerFactory.getLogger(MercadoPagoPaymentServiceImpl.class);

    public MercadoPagoPaymentServiceImpl(@Value("${mercado.pago.url}") String ngrokUrl) {
        this.ngrokUrl = ngrokUrl;
    }

    /**
     * Creamos una preferencia de pago en Mercado Pago.
     * Recibimos los datos de la donación (monto, merendero, donante)
     * y generamos un enlace único para que el donante complete el pago
     */
    @Override
    public String createPreference(PayRequestDto payRequestDto, MerenderoResponseDto merendero) {
        try {
            MercadoPagoConfig.setAccessToken(merendero.getAccessToken());

            // Configuración inicial
            PreferenceBackUrlsRequest backUrls = this.createBackUrls();
            PreferenceItemRequest itemRequest = this.createPreferenceItem(payRequestDto, merendero);
            String notificationUrl = this.createNotificationUrl(payRequestDto);

            // Construcción de la preferencia
            PreferenceRequest preferenceRequest = this.buildPreferenceRequest(
                    itemRequest,
                    notificationUrl,
                    backUrls,
                    merendero,
                    payRequestDto
            );
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            // Retornamos el enlace de pago
            return preference.getInitPoint();
        } catch (MPException | MPApiException e) {
            throw new RuntimeException("Error al crear la preferencia de pago", e);
        }
    }

    /**
     * Procesa las notificaciones de webhook enviadas por Mercado Pago cuando se actualiza el estado de un pago.
     *
     * @param requestBody - JSON de Mercado Pago con datos del evento webhook
     */
    @Override
    public DonationRequestDto processPaymentNotification(String donorEmail, BigDecimal amount, Map<String, Object> requestBody) {
        try {
            String topic = (String) requestBody.get("topic");
            Payment paymentResponse = null;
            // Filtramos solo las notificaciones de tipo "payment"
            if ("payment".equals(topic)) {
                String paymentId = (String) requestBody.get("resource");
                PaymentClient paymentClient = new PaymentClient();
                // Obtenemos información detallada del pago desde Mercado Pago
                paymentResponse = paymentClient.get(Long.parseLong(paymentId));
            }
            if (paymentResponse == null || !"approved".equals(paymentResponse.getStatus())) {
                logger.info("Notificación de pago ignorada - Estado no aprobado");
                return null;
            }
            // Procesamos el pago aprobado
            return processApprovedPayment(paymentResponse, donorEmail, amount);
        } catch (Exception e) {
            throw new RuntimeException("Error procesando notificación de pago", e);
        }
    }

    /**
     * Configurar URLs de retorno
     */
    private PreferenceBackUrlsRequest createBackUrls() {
        return PreferenceBackUrlsRequest.builder()
                .success(this.ngrokUrl + "/list-merenderos")
                .pending(this.ngrokUrl + "/list-merenderos")
                .failure(this.ngrokUrl + "/failure")
                .build();
    }

    /**
     * Mercado Pago llamará a esta URL cuando el pago se complete/falle
     * */
    private String createNotificationUrl(PayRequestDto payRequestDto) {
        return ngrokUrl + "/mercado-pago/notification?donorEmail=" +
                payRequestDto.getDonorEmail() + "&amount=" + payRequestDto.getAmount();
    }

    /**
     *  Crear el producto que se está "vendiendo" (Es una donación en este caso)
     */
    private PreferenceItemRequest createPreferenceItem(PayRequestDto payRequestDto, MerenderoResponseDto merendero) {
        return PreferenceItemRequest.builder()
                .title("Donación a " + merendero.getName())
                .description("Donación a Merendero")
                .categoryId("donations")
                .quantity(1)
                .currencyId("ARS")
                .unitPrice(payRequestDto.getAmount())
                .build();
    }

    /**
     * Construir la preferencia completa para que el usuario pueda donar
     * */
    private PreferenceRequest buildPreferenceRequest(PreferenceItemRequest itemRequest,
                                                     String notificationUrl,
                                                     PreferenceBackUrlsRequest backUrls,
                                                     MerenderoResponseDto merendero,
                                                     PayRequestDto payRequestDto) {
        List<PreferenceItemRequest> items = Collections.singletonList(itemRequest);
        return PreferenceRequest.builder()
                .items(items)
                .notificationUrl(notificationUrl)
                .backUrls(backUrls)
                .marketplace(merendero.getAccessToken())
                .externalReference(payRequestDto.getMerenderoId().toString()) // ID del merendero
                .build();
    }

    /**
     * Procesa un pago aprobado por Mercado Pago y construye el DTO de donación
     * con toda la información financiera relevante para el sistema.
     */
    private DonationRequestDto processApprovedPayment(Payment paymentResponse, String donorEmail, BigDecimal amount) {
        String paymentId = paymentResponse.getId().toString();
        Long merenderoId = Long.parseLong(paymentResponse.getExternalReference());
        logger.info("Procesando pago aprobado: {} para merendero: {}", paymentId, merenderoId);

        // Extraemos otros datos importantes
        BigDecimal grossAmount = paymentResponse.getTransactionAmount(); // Monto bruto donado
        BigDecimal mercadoPagoFee = calculateMercadoPagoFee(paymentResponse); // Comisión de Mercado Pago
        BigDecimal netAmount = grossAmount.subtract(mercadoPagoFee); // Monto recibido por el merendero (gross - fee)

        // Retornamos dto de Donación para que el MPIntegrationController lo guarde
        return DonationRequestDto.builder()
                .paymentId(paymentId)
                .userEmail(donorEmail) // Email registrado en Merendero Facil (no es el email de la cuenta de mp)
                .grossAmount(grossAmount)
                .mpFee(mercadoPagoFee)
                .netAmount(netAmount)
                .merenderoId(merenderoId)
                .donationDate(LocalDateTime.now())
                .build();
    }

    /**
     * Calcular comisiones de Mercado Pago
     */
    private BigDecimal calculateMercadoPagoFee(Payment paymentResponse) {
        return paymentResponse.getFeeDetails().stream()
                .filter(fee -> "mercadopago_fee".equals(fee.getType()))
                .map(PaymentFeeDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Sumamos todas las comisiones MP
    }
}