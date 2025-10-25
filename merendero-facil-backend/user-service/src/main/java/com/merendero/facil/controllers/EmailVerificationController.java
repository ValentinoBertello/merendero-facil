package com.merendero.facil.controllers;

import com.merendero.facil.dto.emailVerification.CodeValidationRequest;
import com.merendero.facil.dto.emailVerification.EmailRequestDto;
import com.merendero.facil.service.EmailVerificationService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador encargado de manejar el envío de mails y validación de codigos
 */
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/verification")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    public EmailVerificationController(EmailVerificationService emailVerificationService) {
        this.emailVerificationService = emailVerificationService;
    }

    /**
     * POST /verification/send-code
     * Envía un código de verificación al email proporcionado.
     */
    @PostMapping("/send-code")
    public ResponseEntity<Void> sendVerificationCode(@Valid @RequestBody EmailRequestDto emailRequestDto) throws MessagingException {
        emailVerificationService.sendVerificationCode(emailRequestDto.getEmail());
        return ResponseEntity.ok().build();
    }

    /**
     * POST /verification/validate-code
     * Valida el código de verificación ingresado por el usuario.
     * @param request DTO con el email y el código a validar.
     * @return true si el código es correcto, false en caso contrario.
     */
    @PostMapping("/validate-code")
    public ResponseEntity<Boolean> validateCode(@RequestBody CodeValidationRequest request) {
        return ResponseEntity.ok(this.emailVerificationService.validateCode(request.getEmail(), request.getCode()));
    }
}
