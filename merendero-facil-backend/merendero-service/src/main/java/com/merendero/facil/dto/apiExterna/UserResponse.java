package com.merendero.facil.dto.apiExterna;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Objeto DTO que representa los datos de un usuario tal como
 * los expone el microservicio de Users.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String lastname;
    private String email;
    private String dni;
    private Boolean active;
    private List<String> roles;
}
