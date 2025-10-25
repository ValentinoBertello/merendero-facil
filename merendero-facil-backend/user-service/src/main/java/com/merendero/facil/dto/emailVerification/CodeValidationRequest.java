package com.merendero.facil.dto.emailVerification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la validación de códigos recibidos por usuarios para cambiar de contraseña
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeValidationRequest {

    private String email;

    private String code;
}
