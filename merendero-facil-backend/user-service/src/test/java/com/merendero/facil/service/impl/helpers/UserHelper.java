package com.merendero.facil.service.impl.helpers;

import com.merendero.facil.dto.UserRequestDto;
import com.merendero.facil.dto.UserResponseDto;
import com.merendero.facil.entities.RoleEntity;
import com.merendero.facil.entities.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

public class UserHelper {

    // Roles
    public static final RoleEntity ROLE_USER = RoleEntity.builder()
            .id(1L)
            .name("USER")
            .build();

    public static final RoleEntity ROLE_ADMIN = RoleEntity.builder()
            .id(2L)
            .name("ADMIN")
            .build();

    public static final RoleEntity ROLE_MANAGER = RoleEntity.builder()
            .id(3L)
            .name("MANAGER")
            .build();

    // UserEntity instances
    public static final UserEntity USER_ENTITY_1 = UserEntity.builder()
            .id(1L)
            .name("Juan")
            .lastname("Pérez")
            .email("juan.perez@gmail.com")
            .password("Secreto123")
            .active(true)
            .roles(List.of(ROLE_USER))
            .createdDate(LocalDateTime.of(2025, 1, 10, 9, 30))
            .lastUpdatedDate(LocalDateTime.of(2025, 1, 15, 14, 45))
            .createdUser(0L)
            .lastUpdatedUser(0L)
            .build();

    public static final UserEntity USER_ENTITY_2 = UserEntity.builder()
            .id(2L)
            .name("María")
            .lastname("Gómez")
            .email("maria.gomez@gmail.com")
            .password("Clave456")
            .active(false)
            .roles(List.of(ROLE_USER, ROLE_ADMIN))
            .createdDate(LocalDateTime.of(2025, 2, 5, 11, 0))
            .lastUpdatedDate(LocalDateTime.of(2025, 2, 6, 16, 20))
            .createdUser(1L)
            .lastUpdatedUser(1L)
            .build();

    public static final UserEntity USER_ENTITY_3 = UserEntity.builder()
            .id(3L)
            .name("Lucas")
            .lastname("Fernández")
            .email("lucas.fernandez@gmail.com")
            .password("Pass789")
            .active(true)
            .roles(List.of(ROLE_MANAGER))
            .createdDate(LocalDateTime.of(2025, 3, 12, 8, 15))
            .lastUpdatedDate(LocalDateTime.of(2025, 3, 13, 9, 50))
            .createdUser(2L)
            .lastUpdatedUser(2L)
            .build();

    // UserResponse instances
    public static final UserResponseDto USER_RESPONSE_1 = UserResponseDto.builder()
            .id(1L)
            .name("Juan")
            .lastname("Pérez")
            .email("juan.perez@gmail.com")
            .active(true)
            .roles(List.of("USER"))
            .build();

    public static final UserResponseDto USER_RESPONSE_2 = UserResponseDto.builder()
            .id(2L)
            .name("María")
            .lastname("Gómez")
            .email("maria.gomez@gmail.com")
            .active(false)
            .roles(List.of("USER", "ADMIN"))
            .build();

    public static final UserResponseDto USER_RESPONSE_3 = UserResponseDto.builder()
            .id(3L)
            .name("Lucas")
            .lastname("Fernández")
            .email("lucas.fernandez@gmail.com")
            .active(true)
            .roles(List.of("MANAGER"))
            .build();


    // UserRequest instances
    public static final UserRequestDto USER_REQUEST_1 = UserRequestDto.builder()
            .name("Sofía")
            .lastname("González")
            .email("sofia.gonzalez@gmail.com")
            .password("Contra123$")
            .roleNames(List.of("USER"))
            .build();

    public static final UserRequestDto USER_REQUEST_2 = UserRequestDto.builder()
            .name("Martín")
            .lastname("Rodríguez")
            .email("martin.rodriguez@hotmail.com")
            .password("ClaveSegura456#")
            .roleNames(List.of("USER", "ADMIN"))
            .build();

    public static final UserRequestDto USER_REQUEST_3 = UserRequestDto.builder()
            .name("Camila")
            .lastname("López")
            .email("camila.lopez@yahoo.com")
            .password("PassWord789!")
            .roleNames(List.of("MANAGER"))
            .build();


    public static final List<UserEntity> ALL_USER_ENTITIES = List.of(
            USER_ENTITY_1,
            USER_ENTITY_2,
            USER_ENTITY_3
    );

    public static final List<UserResponseDto> ALL_USER_RESPONSES = List.of(
            USER_RESPONSE_1,
            USER_RESPONSE_2,
            USER_RESPONSE_3
    );

    public static final List<UserRequestDto> ALL_USER_REQUESTS = List.of(
            USER_REQUEST_1,
            USER_REQUEST_2,
            USER_REQUEST_3
    );
}