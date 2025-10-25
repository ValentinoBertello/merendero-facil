package com.merendero.facil.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;

import java.util.Arrays;

/**
 * Validador que aplica reglas de Passay sobre la contraseña.
 *
 * - Implementa ConstraintValidator<ValidPassword, String>, por eso recibe la anotación y el tipo.
 * - Cuando falla, construye un mensaje de violación con los mensajes que genera Passay.
 */
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * Método que devuelve true si la contraseña cumple todas las reglas; false en caso contrario.
     */
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        // Configuramos las reglas de Passay
        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                new LengthRule(8, 35), // Longitud entre 8 y 30
                new CharacterRule(EnglishCharacterData.UpperCase, 1), // Al menos 1 mayúscula
                new CharacterRule(EnglishCharacterData.LowerCase, 1), // Al menos 1 minúscula
                new CharacterRule(EnglishCharacterData.Digit, 1), // Al menos 1 número
                new CharacterRule(EnglishCharacterData.Special, 1),

                // Regla para bloquear secuencias numéricas (ej: 123, 456)
                //new IllegalSequenceRule(EnglishSequenceData.Numerical, 3, false),

                // Regla para bloquear secuencias alfabéticas (ej: abc, def)
                //new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 3, false),

                new WhitespaceRule() // No espacios en blanco
        ));

        // Ejecutamos la validación contra la contraseña proporcionada
        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true; // La contraseña es válida
        }

        context.disableDefaultConstraintViolation(); // deshabilitamos el mensaje por defecto
        // añadimos un mensaje que concatena los mensajes detallados que genera Passay
        context.buildConstraintViolationWithTemplate(
                String.join(", ", validator.getMessages(result))
        ).addConstraintViolation();

        return false;
    }
}
