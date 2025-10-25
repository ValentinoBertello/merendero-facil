package com.merendero.facil.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Anotación para validar contraseñas con reglas personalizadas.
 *
 * - @Documented: incluye esta anotación en la javadoc de los elementos anotados.
 * - @Constraint(validatedBy = PasswordConstraintValidator.class): indica la clase que
 *   implementa la lógica de validación.
 */
@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface ValidPassword {

    /**
     * Mensaje por defecto que se usará cuando la validación falle.
     */
    String message() default "Contraseña inválida";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
