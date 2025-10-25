package com.merendero.facil.dto.mp;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de solicitud para crear una preferencia de pago en Mercado Pago
 * **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayRequestDto {

    @NotNull
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal amount;

    @NotNull
    @Email
    private String donorEmail;

    @NotNull
    private Long merenderoId;
}
