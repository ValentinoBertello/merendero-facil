package com.merendero.facil.service;

import com.merendero.facil.dto.ResetPasswordDto;
import com.merendero.facil.dto.UserRequestDto;
import com.merendero.facil.dto.UserResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    List<UserResponseDto> getAllUsers();

    UserResponseDto saveUser(UserRequestDto userRequestDto);

    Boolean checkRepeatedEmail(String email);

    UserResponseDto getUserById(Long id);

    void reseatPassword(String email, ResetPasswordDto resetPasswordDto);

    UserResponseDto getUserByEmail(String email);

    UserResponseDto makeUserManager(Long userId);

    UserResponseDto deleteUserById(Long userId);
}
