package com.merendero.facil.service.impl;

import com.merendero.facil.dto.ResetPasswordDto;
import com.merendero.facil.dto.UserResponseDto;
import com.merendero.facil.entities.RoleEntity;
import com.merendero.facil.entities.UserEntity;
import com.merendero.facil.mapper.UserDataMapper;
import com.merendero.facil.repository.RoleRepository;
import com.merendero.facil.repository.UserRepository;
import com.merendero.facil.service.impl.helpers.UserHelper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.merendero.facil.service.impl.helpers.UserHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserDataMapper userDataMapper;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(ALL_USER_ENTITIES);
        when(userDataMapper.mapUserEntitiesToUserResponses(ALL_USER_ENTITIES)).thenReturn(ALL_USER_RESPONSES);
        List<UserResponseDto> result = this.userServiceImpl.getAllUsers();

        assertEquals("Juan", result.get(0).getName());
        assertEquals("María", result.get(1).getName());
    }

    @Test
    void saveUser() {
        var req = UserHelper.USER_REQUEST_2;
        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(roleRepository.findByNameIn(any())).thenReturn(USER_ENTITY_2.getRoles());
        when(userDataMapper.mapUserRequestToUserEntity(any(),any(),any())).thenReturn(USER_ENTITY_2);
        when(userRepository.save(USER_ENTITY_2)).thenReturn(USER_ENTITY_2);
        when(userDataMapper.mapUserEntityToUserResponse(USER_ENTITY_2)).thenReturn(USER_RESPONSE_2);

        UserResponseDto result = this.userServiceImpl.saveUser(USER_REQUEST_2);

        assertEquals("maria.gomez@gmail.com", result.getEmail());
    }

    @Test
    void saveUserException() {
        var req = UserHelper.USER_REQUEST_2;
        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(roleRepository.findByNameIn(any())).thenReturn(USER_ENTITY_1.getRoles());


        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> userServiceImpl.saveUser(USER_REQUEST_2));

    }

    @Test
    void checkRepeatedEmail() {
        when(userRepository.existsByEmail("valen@gmail.com")).thenReturn(true);
        Boolean exists = userServiceImpl.checkRepeatedEmail("valen@gmail.com");
        assertTrue(exists);
        verify(userRepository).existsByEmail("valen@gmail.com");
    }

    @Test
    void getUserById() {
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(UserHelper.USER_ENTITY_1));
        when(userDataMapper.mapUserEntityToUserResponse(UserHelper.USER_ENTITY_1))
                .thenReturn(UserHelper.USER_RESPONSE_1);

        UserResponseDto resp = userServiceImpl.getUserById(1L);
        assertNotNull(resp);
        assertEquals("Juan", resp.getName());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserByEmail() {
        when(userRepository.findByEmail("juan.perez@gmail.com"))
                .thenReturn(java.util.Optional.of(UserHelper.USER_ENTITY_1));
        when(userDataMapper.mapUserEntityToUserResponse(UserHelper.USER_ENTITY_1))
                .thenReturn(UserHelper.USER_RESPONSE_1);

        UserResponseDto resp = userServiceImpl.getUserByEmail("juan.perez@gmail.com");
        assertEquals("Juan", resp.getName());
        verify(userRepository).findByEmail("juan.perez@gmail.com");
    }

    @Test
    void getUserByEmailEx() {
        when(userRepository.findByEmail("juan.perez@gmail.com"))
                .thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> userServiceImpl.getUserByEmail("juan.perez@gmail.com")
        );
    }

    @Test
    void makeUserManager_newRoleAdded() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setRoles(new ArrayList<>(List.of(UserHelper.ROLE_USER)));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        RoleEntity modRole = RoleEntity.builder().id(4L).name("ROLE_ENCARGADO").build();
        when(roleRepository.findByName("ROLE_ENCARGADO")).thenReturn(modRole);
        when(userDataMapper.mapUserEntityToUserResponse(user)).thenReturn(UserHelper.USER_RESPONSE_1);

        UserResponseDto response = userServiceImpl.makeUserManager(1L);

        assertTrue(user.getRoles().contains(modRole));
        verify(userRepository).save(user);
        assertEquals(UserHelper.USER_RESPONSE_1, response);
    }

    @Test
    void deleteUserById() {
        UserEntity user = UserHelper.USER_ENTITY_2;
        when(userRepository.findById(2L)).thenReturn(java.util.Optional.of(user));
        when(userDataMapper.mapUserEntityToUserResponse(user))
                .thenReturn(UserHelper.USER_RESPONSE_2);

        UserResponseDto resp = userServiceImpl.deleteUserById(2L);

        assertEquals("María", resp.getName());
        verify(userRepository).deleteById(2L);
    }

    @Test
    void whenEmailExists_thenThrowException() {
        var req = USER_REQUEST_1;
        when(userRepository.existsByEmail(req.getEmail())).thenReturn(true);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userServiceImpl.validateEmailUserRequest(req.getEmail())
        );
        assertEquals("El email ya está registrado", ex.getMessage());
        verify(userRepository).existsByEmail(req.getEmail());
    }

    @Test
    void whenEmailNotExists_thenNoException() {
        var req = UserHelper.USER_REQUEST_2;
        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        assertDoesNotThrow(() -> userServiceImpl.validateEmailUserRequest(req.getEmail()));
        verify(userRepository).existsByEmail(req.getEmail());
    }

    @Test
    void reseatPassword() {
        String email = UserHelper.USER_ENTITY_1.getEmail();
        UserEntity user = UserEntity.builder()
                .email(email)
                .password("oldPass")
                .build();

        ResetPasswordDto dto = mock(ResetPasswordDto.class);
        when(dto.getPassword()).thenReturn("miNuevaPass");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("miNuevaPass")).thenReturn("encoded-miNuevaPass");

        userServiceImpl.reseatPassword(email, dto);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository, times(1)).save(captor.capture());
        UserEntity saved = captor.getValue();

        assertEquals("encoded-miNuevaPass", saved.getPassword());
    }

    @Test
    void reseatPassword_userNotFound_throwsEntityNotFound() {
        String email = "sinexistir@example.com";
        ResetPasswordDto dto = mock(ResetPasswordDto.class);
        when(dto.getPassword()).thenReturn("otraPass");
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class,
                () -> userServiceImpl.reseatPassword(email, dto));

        verify(userRepository, never()).save(any());
    }
}