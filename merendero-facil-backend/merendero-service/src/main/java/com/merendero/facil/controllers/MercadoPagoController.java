package com.merendero.facil.controllers;

import com.merendero.facil.dto.donation.DonationRequestDto;
import com.merendero.facil.dto.merendero.MerenderoResponseDto;
import com.merendero.facil.dto.mp.MpLink;
import com.merendero.facil.dto.mp.PayRequestDto;
import com.merendero.facil.service.DonationService;
import com.merendero.facil.service.MercadoPagoAuthService;
import com.merendero.facil.service.MercadoPagoPaymentService;
import com.merendero.facil.service.MerenderoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Controlador encargado de gestionar la integración completa con Mercado Pago
 */
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/mercado-pago")
public class MercadoPagoController {
    
    private final MercadoPagoPaymentService mercadoPagoPaymentService;
    private final MercadoPagoAuthService mercadoPagoAuthService;
    private final MerenderoService merenderoService;
    private final DonationService donationService;

    public MercadoPagoController(MercadoPagoPaymentService mercadoPagoPaymentService,
                                 MercadoPagoAuthService mercadoPagoAuthService,
                                 MerenderoService merenderoService,
                                 DonationService donationService) {
        this.mercadoPagoPaymentService = mercadoPagoPaymentService;
        this.mercadoPagoAuthService = mercadoPagoAuthService;
        this.merenderoService = merenderoService;
        this.donationService = donationService;
    }

    /**
     * POST /mercado-pago/preference
     * Endpoint para crear una preferencia de pago en Mercado Pago.
     * Generamos un enlace único para que el donante complete el pago
     **/
    @PostMapping("/preference")
    public ResponseEntity<MpLink> createPaymentPreference(@Valid @RequestBody PayRequestDto payRequestDto) {
        MerenderoResponseDto merendero = merenderoService.getMerenderoById(payRequestDto.getMerenderoId());
        String paymentLink = mercadoPagoPaymentService.createPreference(payRequestDto, merendero);
        return ResponseEntity.ok(MpLink.builder().link(paymentLink).build());
    }

    /**
     * POST /mercado-pago/notification
     * Mercado Pago le pega a este endpoint luego de que un pago se termine de procesar.
     * Pasándonos toda la información del pago para que podamos registrarla.
     **/
    @PostMapping("/notification")
    public ResponseEntity<String> handlePaymentNotification( @RequestParam("donorEmail") String donorEmail,
                                                             @RequestParam("amount") BigDecimal amount,
                                                             @RequestBody Map<String, Object> requestBody) {
        DonationRequestDto donationRequest = mercadoPagoPaymentService.processPaymentNotification(
                donorEmail, amount, requestBody);
        if (donationRequest == null) {
            return ResponseEntity.ok().body("Notification received but no action taken");
        }
        donationService.saveDonation(donationRequest);
        return ResponseEntity.ok("OK");
    }

    /**
     * GET /mercado-pago/authorize
     * Este endpoint genera
     * un enlace que redirige al dueño del merendero a Mercado Pago
     * para autorizar a nuestra aplicación a actuar en su nombre y que pueda recibir donaciones.
     *
     * @param state - Datos del merendero codificados (JSON → Base64 → URL encoding)
     */
    @GetMapping("/authorize")
    public ResponseEntity<MpLink> generateAuthorizationLink(@RequestParam String state) {
        String authUrl = mercadoPagoAuthService.generateAuthorizationUrl(state);
        return ResponseEntity.ok(MpLink.builder().link(authUrl).build());
    }

    /**
     * GET /mercado-pago/callback
     * Endpoint de CALLBACK donde Mercado Pago redirige después de la autorización
     *
     * @param code - Código de autorización temporario proporcionado por Mercado Pago
     * @param state - MISMO state que se envió originalmente, con los datos del merendero
     */
    @GetMapping("/callback") ResponseEntity<Void> handleOAuthCallback(@RequestParam String code,
                                                                         @RequestParam String state) {
        this.mercadoPagoAuthService.processOAuthCallback(code, state);
        // Redirigimos al usuario de vuelta a la aplicación frontend
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "http://localhost:4200/merendero-ok")
                .build();
    }
}