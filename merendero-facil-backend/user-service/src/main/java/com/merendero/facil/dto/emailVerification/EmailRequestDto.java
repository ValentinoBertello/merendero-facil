package com.merendero.facil.dto.emailVerification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase recibida desde el front a la hora de crear un user o resetear contraseña
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailRequestDto {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    @Size(max = 100, message = "El email no puede superar 100 caracteres")
    private String email;
}
