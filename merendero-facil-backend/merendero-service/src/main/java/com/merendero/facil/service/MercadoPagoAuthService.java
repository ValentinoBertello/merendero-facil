package com.merendero.facil.service;

public interface MercadoPagoAuthService {
    String generateAuthorizationUrl(String state);

    void processOAuthCallback(String code, String state);
}
