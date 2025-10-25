package com.merendero.facil.service.impl;

import com.merendero.facil.dto.ResetPasswordDto;
import com.merendero.facil.dto.UserRequestDto;
import com.merendero.facil.dto.UserResponseDto;
import com.merendero.facil.entities.RoleEntity;
import com.merendero.facil.entities.UserEntity;
import com.merendero.facil.mapper.UserDataMapper;
import com.merendero.facil.repository.RoleRepository;
import com.merendero.facil.repository.UserRepository;
import com.merendero.facil.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Toda la lógica de negocio referida a los usuarios que
 * de la aplicación.
 * **/
@Service
public class UserServiceImpl implements UserService {

    private final UserDataMapper userDataMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserDataMapper userDataMapper, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userDataMapper = userDataMapper;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Obtiene todos los usuarios registrados en la base de datos (ADMIN).
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        List<UserEntity> userEntities = this.userRepository.findAll();
        return this.userDataMapper.mapUserEntitiesToUserResponses(userEntities);
    }

    /**
     * Crea y persiste un nuevo usuario en la base de datos.
     */
    @Override
    @Transactional
    public UserResponseDto saveUser(UserRequestDto userRequestDto) {
        this.validateEmailUserRequest(userRequestDto.getEmail());
        List<RoleEntity> roles = this.validatRolesUserRequest(userRequestDto);

        // Mapeamos el userRequest a entity mediante la clase userDataMapper
        UserEntity userEntity = this.userDataMapper.mapUserRequestToUserEntity(
                userRequestDto,
                roles,
                this.passwordEncoder.encode(userRequestDto.getPassword()));

        userEntity = this.userRepository.save(userEntity);
        return this.userDataMapper.mapUserEntityToUserResponse(userEntity);
    }

    /**
     * Revisamos si el email recibido ya existe
     */
    @Override
    @Transactional(readOnly = true)
    public Boolean checkRepeatedEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Busca un usuario por su email.
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        return this.userDataMapper.mapUserEntityToUserResponse(this.getUserEntityById(id));
    }

    /**
     * Permite resetear la contraseña de un usuario.
     */
    @Override
    @Transactional
    public void reseatPassword(String email, ResetPasswordDto resetPasswordDto) {
        String encodedPassword = this.passwordEncoder.encode(resetPasswordDto.getPassword());
        UserEntity userEntity = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con email " + email + " no existe"));
        userEntity.setPassword(encodedPassword);
        userRepository.save(userEntity);
    }


    /**
     * Busca un usuario por su email.
     */
    @Override
    public UserResponseDto getUserByEmail(String email) {
        UserResponseDto userResponseDto = this.userDataMapper.mapUserEntityToUserResponse(userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario inexistente")));
        return userResponseDto;
    }

    /**
     * Asigna el rol de encargado (ROLE_ENCARGADO) a un usuario.
     */
    @Override
    @Transactional
    public UserResponseDto makeUserManager(Long userId) {
        UserEntity userEntity = getUserEntityById(userId);
        RoleEntity moderatorRole = roleRepository.findByName("ROLE_ENCARGADO");

        // Verificamos que el user no tenga el rol de encargado
        if (userEntity.getRoles().contains(moderatorRole)) {
            throw new IllegalArgumentException("El usuario ya tiene el rol de encargado");
        }
        userEntity.getRoles().add(moderatorRole);
        userRepository.save(userEntity);

        return userDataMapper.mapUserEntityToUserResponse(userEntity);
    }

    /**
     * Elimina un usuario por su ID.
     */
    @Override
    @Transactional
    public UserResponseDto deleteUserById(Long userId) {
        UserEntity userEntity = this.getUserEntityById(userId);
        this.userRepository.deleteById(userId);
        return this.userDataMapper.mapUserEntityToUserResponse(userEntity);
    }

    /**
     * Validamos que el email del usuario nuevo, no este registrado
     * */
    public void validateEmailUserRequest(String userRequestEmail) {
        if (userRepository.existsByEmail(userRequestEmail)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
    }

    /**
     * Comprobamos que todos los roles solicitados existen en la base de datos
     * */
    public List<RoleEntity> validatRolesUserRequest(UserRequestDto userRequestDto) {
        List<RoleEntity> roles = roleRepository.findByNameIn(userRequestDto.getRoleNames());
        if (roles.size() != userRequestDto.getRoleNames().size()) {
            throw new EntityNotFoundException("Algunos roles no existen");
        }
        return roles;
    }

    /**
     * Busca un UserEntity por su id
     * */
    public UserEntity getUserEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario inexistente"));
    }
}
