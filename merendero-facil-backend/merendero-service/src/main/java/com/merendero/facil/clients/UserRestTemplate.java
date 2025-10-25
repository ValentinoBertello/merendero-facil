package com.merendero.facil.clients;

import com.merendero.facil.dto.apiExterna.UserResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * Cliente REST ligero para comunicarse con el microservicio de usuarios.
 * - Usa RestTemplate para realizar llamadas HTTP al servicio de Users.
 */
@Service
public class UserRestTemplate {

    private final RestTemplate restTemplate;

    public UserRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    String baseUrl = "http://localhost:8080/users/";

    /**
     * Llama al endpoint GET /users/check-email/{email} para comprobar si existe el email.
     */
    public Boolean checkEmailExists(String email){
        return Objects.requireNonNull
                (this.restTemplate.getForEntity(this.baseUrl + "check-email/" + email,
                        Boolean.class).getBody());
    }

    /**
     * Llama al endpoint GET /users/email/{email} y devuelve el UserResponse.
     */
    public UserResponse getUserByEmail(String email){
        return Objects.requireNonNull
                (this.restTemplate.getForEntity(this.baseUrl + "email/" + email,
                        UserResponse.class).getBody());
    }

    /**
     * Llama al endpoint PUT /users/make/manager/{userId} para asignar rol de manager.
     */
    public UserResponse setManagerRole(Long userId){
        String url = this.baseUrl + "make/manager/" + userId;
        ResponseEntity<UserResponse> resp = this.restTemplate.exchange(
                url,
                HttpMethod.PUT,
                null,
                UserResponse.class
        );
        return Objects.requireNonNull(resp.getBody());
    }
}