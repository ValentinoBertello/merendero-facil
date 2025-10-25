package com.merendero.facil.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad que representa a un usuario del sistema.
 * Relación ManyToMany con RoleEntity mediante tabla intermedia "users_roles".
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String lastname;

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Column
    private Boolean active;

    @ManyToMany
    @JoinTable(
        name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            uniqueConstraints = { @UniqueConstraint(columnNames = {"user_id", "role_id"})}
    )
    private List<RoleEntity> roles;

    @Column(name = "created_datetime")
    private LocalDateTime createdDate;

    @Column(name = "last_updated_datetime")
    private LocalDateTime lastUpdatedDate;

    @Column(name = "created_user")
    private Long createdUser;

    @Column(name = "last_updated_user")
    private Long lastUpdatedUser;

    @PrePersist
    public void beforePersist() {
        createdDate = LocalDateTime.now();
    }

    @PreUpdate
    public void beforeUpdate() {
        lastUpdatedDate = LocalDateTime.now();
    }
}
