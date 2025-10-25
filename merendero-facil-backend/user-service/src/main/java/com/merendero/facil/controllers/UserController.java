package com.merendero.facil.controllers;

import com.merendero.facil.dto.ResetPasswordDto;
import com.merendero.facil.dto.UserRequestDto;
import com.merendero.facil.dto.UserResponseDto;
import com.merendero.facil.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /users
     * Devuelve la lista completa de usuarios registrados (ADMIN).
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers(){
        return ResponseEntity.ok(this.userService.getAllUsers());
    }

    /**
     * GET /users/email/{email}
     * Busca y devuelve un usuario a partir de su email.
     */
    @GetMapping("email/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email){
        return ResponseEntity.ok(this.userService.getUserByEmail(email));
    }

    /**
     * GET /users/check-email/{email}
     * Verifica si un correo electrónico ya se encuentra registrado.
     */
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Boolean> checkRepeatedEmail(@PathVariable String email){
        return ResponseEntity.ok(this.userService.checkRepeatedEmail(email));
    }

    /**
     * POST /users/register
     * Registra un nuevo usuario desde el front.
     * Se eliminan manualmente roles administradores para evitar que se autoadjudiquen privilegios.
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody @Valid UserRequestDto userRequestDto){
        userRequestDto.getRoleNames().removeIf(role -> role.equals("ROLE_ADMIN"));
        return ResponseEntity.ok(this.userService.saveUser(userRequestDto));
    }

    /**
     * PUT /users/make/manager/{userId}
     * Convierte a un usuario en "manager" a partir de su ID.
     */
    @PutMapping("/make/manager/{userId}")
    public ResponseEntity<UserResponseDto> makeUserManager(@PathVariable Long userId){
        return ResponseEntity.ok(this.userService.makeUserManager(userId));
    }

    /**
     * PUT /users/reset-password/{email}
     * Permite resetear la contraseña de un usuario a partir de su email.
     */
    @PutMapping("/reset-password/{email}")
    public ResponseEntity<Void> reseatPassword(@PathVariable String email,
                                                           @RequestBody @Valid ResetPasswordDto resetPasswordDto){
        this.userService.reseatPassword(email, resetPasswordDto);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE /users/delete/{userId}
     * Elimina un usuario por su ID.
     */
    @DeleteMapping("delete/{userId}")
    public ResponseEntity<UserResponseDto> deleteUserById(@PathVariable Long userId){
        return ResponseEntity.ok(this.userService.deleteUserById(userId));
    }

    /**
     * GET /users/auth/me
     * Devuelve el perfil del usuario actualmente autenticado.
     * **/
    @GetMapping("/auth/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(this.userService.getUserByEmail(authentication.getName()));
    }
}
