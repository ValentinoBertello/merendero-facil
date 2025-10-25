package com.merendero.facil.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * GlobalExceptionHandler se encarga de capturar y manejar de forma centralizada
 * las excepciones que ocurren en los controladores.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Maneja las excepciones de validación (cuando falla @Valid en un request)
     * **/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String additionalInfo = getStackTraceAsString(ex);
        BindingResult result = ex.getBindingResult();
        List<String> errors = result.getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        String errorMessage = "Errors: " + String.join(", ", errors);
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), errorMessage, additionalInfo);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja excepciones cuando se lanza un IllegalArgumentException (por ejemplo, argumentos inválidos manuales)
     * **/
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        String additionalInfo = getStackTraceAsString(ex);
        String mensaje = ex.getMessage();
        String errorMessage = "Errors: " + mensaje;
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), errorMessage, additionalInfo);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja excepciones cuando se lanza un EntityNotFoundException
     * **/
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        String additionalInfo = getStackTraceAsString(ex);
        String mensaje = ex.getMessage();
        String errorMessage = "Errors: " + mensaje;
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), errorMessage, additionalInfo);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Devuelve cualquier otra excepción no controlada previamente y además
     * devuelve la causa RAÍZ en el mensaje
     * **/
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        // Inicializamos root en la excepción general (capa superior)
        Throwable root = ex;
        // Vamos iterando causa por causa hasta llegar a la causa raíz
        while (root.getCause() != null) {
            root = root.getCause();
        }

        // Formamos un mensaje con el tipo de excepción y su mensaje
        String realMessage = root.getClass().getSimpleName() + ": " + root.getMessage();

        // Seguimos incluyendo el stacktrace completo como additionalInfo
        String additionalInfo = getStackTraceAsString(ex);

        // Construimos el response con el mensaje real
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                realMessage,
                additionalInfo
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    private String getStackTraceAsString(Throwable ex) {
        StringBuilder traceBuilder = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            traceBuilder.append(element.toString()).append("\r\n");
        }
        return traceBuilder.toString();
    }
}


