package com.merendero.facil.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 *  Marca esta clase como un manejador global de excepciones para todos los controladores
 * **/
@ControllerAdvice
public class GlobalExceptionHandler {

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