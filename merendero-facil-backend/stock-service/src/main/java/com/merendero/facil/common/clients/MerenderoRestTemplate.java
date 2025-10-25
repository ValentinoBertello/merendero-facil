package com.merendero.facil.common.clients;

import com.merendero.facil.common.clients.dto.DonationDateSummary;
import com.merendero.facil.common.clients.dto.MerenderoResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Servicio que utiliza RestTemplate para comunicarse con el microservicio de merenderos y donaciones
 **/
@Service
public class MerenderoRestTemplate {

    private final RestTemplate restTemplate;

    public MerenderoRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    String baseUrl = "http://localhost:8081/";

    /**
     * Obtener información de un merendero específico por su ID.
     * **/
    public MerenderoResponse getMerenderoById(Long id) {
        try {
            return Objects.requireNonNull(
                    this.restTemplate.getForEntity(baseUrl + "merenderos/byId/" + id, MerenderoResponse.class)
                            .getBody()
            );
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Merendero no existe");
        } catch (RestClientException e) {
            throw new RuntimeException("Error al comunicarse con el microservicio de merenderos: " + e.getMessage());
        }
    }

    /**
     * Obtiene las donaciones agrupadas por período desde el microservicio de donaciones
     * Propagando el token de autenticación para autorización entre servicios
     */
    public List<DonationDateSummary> getDonationsByMerenderoAndDates(Long merenderoId, LocalDate startDate,
                                                                     LocalDate endDate, String groupBy,
                                                                     String authorizationHeader) {
        try {
            // Url del enpoint de donaciones
            String url = baseUrl + "/reports/grouped-summary/" + merenderoId + "/" + startDate + "/" + endDate +
                    "/group/" + groupBy;

            // Configura el headers HTTP  incluyendo el token de autorización
            HttpHeaders headers = new HttpHeaders();
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                headers.set("Authorization", authorizationHeader);
            }

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Realiza la llamada REST
            ResponseEntity<DonationDateSummary[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, DonationDateSummary[].class);

            return Arrays.asList(Objects.requireNonNull(response.getBody()));
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Merendero no existe");
        } catch (RestClientException e) {
            throw new RuntimeException("Error al comunicarse con el microservicio de merenderos: " + e.getMessage());
        }
    }
}