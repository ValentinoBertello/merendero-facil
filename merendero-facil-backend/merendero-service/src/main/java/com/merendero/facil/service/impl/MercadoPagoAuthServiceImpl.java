package com.merendero.facil.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.merendero.facil.dto.merendero.MerenderoRequestDto;
import com.merendero.facil.service.MercadoPagoAuthService;
import com.merendero.facil.service.MerenderoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * Servicio para la gestión de la autorización OAuth 2.0 de Mercado Pago.
 * Este servicio permite que la aplicación opere en nombre de los merenderos
 * para recibir donaciones a través de Mercado Pago Marketplace.
 */
@Service
public class MercadoPagoAuthServiceImpl implements MercadoPagoAuthService {

    private final String clientId;
    private final String clientSecret;
    // URL pública para OAuth callback (un tunel) - Mercado Pago redirige aquí después de autorizar
    private final String ngrokUrl;
    private final MerenderoService merenderoService;
    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(MercadoPagoAuthServiceImpl.class);

    public MercadoPagoAuthServiceImpl(@Value("${mercado.pago.client.id}") String clientId,
                                      @Value("${mercado.pago.client.secret}") String clientSecret,
                                      @Value("${mercado.pago.url}") String ngrokUrl, MerenderoService merenderoService, RestTemplate restTemplate) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.ngrokUrl = ngrokUrl;
        this.merenderoService = merenderoService;
        this.restTemplate = restTemplate;
    }

    /**
     * Generamos un enlace que redirige al dueño del merendero a Mercado Pago
     * para que autorice a nuestra aplicación a actuar en su nombre (recibir donaciones).
     *
     * @param state - Datos del merendero codificados (en formato JSON → Base64 → URL encoding)
     */
    @Override
    public String generateAuthorizationUrl(String state) {
        // URL donde Mercado Pago nos notificará cuando el usuariose autorice
        String redirectUri = URLEncoder.encode
                (ngrokUrl + "/mercado-pago/callback", StandardCharsets.UTF_8);

        // Retornamos la URL de autorización con todos los parámetros requeridos por Mercado Pago
        return String.format(
                "https://auth.mercadopago.com/authorization?client_id=%s&response_type=code&platform_id=mp" +
                        "&redirect_uri=%s&state=%s&prompt=consent",
                clientId,
                redirectUri,
                URLEncoder.encode(state, StandardCharsets.UTF_8)
        );
    }

    /**
     * Procesa el callback de OAuth después de que el usuario autoriza a nuestra aplicación.
     *
     * @param code - Código de autorización temporario proporcionado por Mercado Pago
     * @param state - State original con los datos del merendero codificados
     */
    @Override
    public void processOAuthCallback(String code, String state) {
        try {
            // Decodificamos los datos del merendero
            MerenderoRequestDto merenderoData = decodeStateToMerenderoRequest(state);

            // Canjeamos el código temporario por un access token permanente
            Map<String, Object> token = exchangeCodeForToken(code);

            // Guardamos el merendero en nuestra base de datos junto con su token de acceso
            this.merenderoService.createMerenderoWithAccessToken(merenderoData, (String) token.get("access_token"));
        } catch (Exception e) {
            logger.error("Error procesando callback de OAuth", e);
            throw new RuntimeException("Error procesando callback de autorización", e);
        }
    }

    /**
     * Decodifica el "state" codificado en Base64 y lo convierte en un objeto MerenderoRequestDto
     */
    private MerenderoRequestDto decodeStateToMerenderoRequest(String encodedState) throws JsonProcessingException {
        String stateJson = new String(Base64.getDecoder().decode(encodedState));
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper.readValue(stateJson, MerenderoRequestDto.class);
    }

    /**
     * Intercambiamos un código de autorización de OAuth por un token de acceso permanente de Mercado Pago.
     * Obteniendo asi, credenciales de acceso para operar en nombre del usuario.
     */
    private Map<String, Object> exchangeCodeForToken(String code) {
        String redirectUri = ngrokUrl + "/mercado-pago/callback";
        String url = "https://api.mercadopago.com/oauth/token";

        // Parámetros del cuerpo de la solicitud
        MultiValueMap<String, String> params = this.createTokenExchangeParams(code, redirectUri);

        // Headers
        HttpHeaders headers = this.createTokenExchangeHeaders();

        // Crear la entidad HTTP
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // Hacer la solicitud POST y recibir el Access Token permanente
        ResponseEntity<Map> response = this.restTemplate.postForEntity(url, request, Map.class);
        return response.getBody();
    }

    /**
     * Crea los parámetros para el intercambio del codigo por el access token
     */
    private MultiValueMap<String, String> createTokenExchangeParams(String code, String redirectUri) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", redirectUri);
        return params;
    }

    /**
     * Crea los headers para la solicitud de intercambio de token
     */
    private HttpHeaders createTokenExchangeHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }
}
