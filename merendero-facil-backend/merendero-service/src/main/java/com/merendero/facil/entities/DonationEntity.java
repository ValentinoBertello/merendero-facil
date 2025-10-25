package com.merendero.facil.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa a una donación del sistema.
 */
@Entity
@Table(name = "donations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_email")
    private String userEmail;

    @ManyToOne
    @JoinColumn(name = "merendero_id")
    private MerenderoEntity merendero;

    @Column(name = "donation_date")
    private LocalDateTime donationDate;

    /**
     * Identificador único de la transacción proporcionado por Mercado Pago
     */
    @Column(name = "payment_id", unique = true)
    private String paymentId;

    @Column(name = "gross_amount") // Monto bruto donado (ej: $1000)
    private BigDecimal grossAmount;

    @Column(name = "mp_fee") // Comisión de Mercado Pago (ej: $50)
    private BigDecimal mpFee;

    @Column(name = "net_amount") // Monto recibido por el merendero (gross - fee)
    private BigDecimal netAmount;
}
