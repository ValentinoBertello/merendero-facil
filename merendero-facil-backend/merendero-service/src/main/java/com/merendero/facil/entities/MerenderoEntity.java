package com.merendero.facil.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Entidad que representa a un merendero del sistema.
 */
@Entity
@Table(name = "merenderos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerenderoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String address;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 8)
    private BigDecimal longitude;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "days_open", nullable = false)
    private String daysOpen;

    @Column(name = "opening_time", nullable = false)
    private LocalTime openingTime;

    @Column(name = "closing_time", nullable = false)
    private LocalTime closingTime;

    @Column(name = "manager_id", nullable = false)
    private Long managerId;

    @Column(name = "manager_email", nullable = false)
    private String managerEmail;

    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "merendero", cascade = CascadeType.ALL)
    private List<DonationEntity> donations;

    @Column(name = "access_token", nullable = true)
    private String accessToken;

    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime;
    @Column(name = "last_updated_datetime")
    private LocalDateTime lastUpdatedDatetime;
    @Column(name = "created_user")
    private Long createdUser;
    @Column(name = "last_updated_user")
    private Long lastUpdatedUser;

    @PrePersist
    public void beforePersist() {
        lastUpdatedDatetime = LocalDateTime.now();
        createdDatetime = LocalDateTime.now();
    }

    @PreUpdate
    public void beforeUpdate() {
        lastUpdatedDatetime = LocalDateTime.now();
    }
}
