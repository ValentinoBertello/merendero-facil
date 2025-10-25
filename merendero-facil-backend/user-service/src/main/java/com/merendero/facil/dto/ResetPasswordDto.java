package com.merendero.facil.dto;

import com.merendero.facil.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO usado para la nueva contraseña recibida desde el frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordDto {
    @NotBlank(message = "La contraseña es obligatoria")
    @ValidPassword
    private String password;
}
