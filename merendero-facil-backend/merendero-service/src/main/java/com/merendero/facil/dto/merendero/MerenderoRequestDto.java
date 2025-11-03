package com.merendero.facil.dto.merendero;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * DTO de solicitud para la creación de un Merendero.
 * **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerenderoRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String name;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(min = 2, max = 100, message = "La dirección debe tener entre 2 y 100 caracteres")
    private String address;

    @NotNull(message = "La latitud es obligatoria")
    private BigDecimal latitude;

    @NotNull(message = "La longitude es obligatoria")
    private BigDecimal longitude;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    @Max(value = 500, message = "La capacidad no puede ser mayor a 500")
    private Integer capacity;

    @NotBlank(message = "Los días de apertura son obligatorios")
    // Validás formato libre, pero podrías validar valores permitidos con lógica extra si querés
    private String daysOpen;

    @NotNull(message = "La hora de apertura es obligatoria")
    private LocalTime openingTime;

    @NotNull(message = "La hora de cierre es obligatoria")
    private LocalTime closingTime;

    private Long createdUser;

    // Recibimos el mail del usuario que creará un merendero
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    @Size(min = 5, max = 100, message = "El email debe tener entre 5 y 100 caracteres")
    private String managerEmail;
}