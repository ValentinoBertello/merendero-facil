package com.merendero.facil.mapper;

import com.merendero.facil.dto.UserRequestDto;
import com.merendero.facil.dto.UserResponseDto;
import com.merendero.facil.entities.RoleEntity;
import com.merendero.facil.entities.UserEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * La clase "UserDataMapper" se encarga de mapear usuarios "request", "entity" y "response"
 * entre ellos.
 */
@Component
public class UserDataMapper {

    /**
     * Mapea una entidad de usuario {@link UserEntity} a su DTO de respuesta {@link UserResponseDto}.
     */
    public UserResponseDto mapUserEntityToUserResponse(UserEntity userEntity) {
        return UserResponseDto.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                    .lastname(userEntity.getLastname())
                    .email(userEntity.getEmail())
                .active(userEntity.getActive())
                .roles(mapRolesToStringList(userEntity.getRoles()))
                .build();
    }

    /**
     * Convierte una lista de entidades de rol {@link RoleEntity} en una lista de nombres de rol.
     */
    private List<String> mapRolesToStringList(List<RoleEntity> roles) {
        return roles.stream()
                .map(RoleEntity::getName)
                .toList();
    }

    /**
     * Mapea una lista de entidades de usuario a una lista de DTOs de respuesta.
     */
    public List<UserResponseDto> mapUserEntitiesToUserResponses(List<UserEntity> userEntities) {
        List<UserResponseDto> responses = new ArrayList<>();
        for (UserEntity uE : userEntities){
            responses.add(this.mapUserEntityToUserResponse(uE));
        }
        return responses;
    }

    /**
     * Mapea los datos recibidos en el DTO de petición {@link UserRequestDto} a una nueva entidad de usuario.
     *
     * @param userRequestDto       DTO con los datos ingresados por el cliente (nombre, email, etc.)
     * @param roles             lista de entidades de rol asignadas al usuario
     * @param encodedPassword   contraseña ya cifrada para almacenar en la entidad
     */
    public UserEntity mapUserRequestToUserEntity(UserRequestDto userRequestDto, List<RoleEntity> roles,
                                                 String encodedPassword) {
        return UserEntity.builder()
                .name(userRequestDto.getName())
                .lastname(userRequestDto.getLastname())
                .email(userRequestDto.getEmail())
                .password(encodedPassword)
                .roles(roles)
                .active(true)
                .build();
    }
}