package com.merendero.facil.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Configuración para habilitar seguridad a nivel de métodos (soporta @PreAuthorize, @PostAuthorize, etc.)
 **/
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig {
}
