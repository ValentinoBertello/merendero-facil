package com.merendero.facil.common.security;

import com.merendero.facil.auth.JwtValidationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * Configuración principal de seguridad para la aplicación.
 * Define las reglas de acceso y los comportamientos de seguridad globales.
 */
@Configuration
//@EnableMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;

    public SpringSecurityConfig(AuthenticationConfiguration authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
    }

    /**
     * Crea y configura el AuthenticationManager que será usado por:
     * - El filtro de autenticación JWT
     * - Cualquier proceso de autenticación en la aplicación
     */
    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura las reglas de seguridad HTTP:
     * - Qué endpoints son públicos y cuáles requieren autenticación
     * - Protección contra CSRF (desactivada para APIs REST)
     * - Política de sesiones (sin estado para APIs REST)
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        DefaultSecurityFilterChain build = http.authorizeHttpRequests((authz) ->
                        authz
                                .requestMatchers(HttpMethod.GET, "/auth/**", "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui/index.html").permitAll()

                                // supplies
                                .requestMatchers(HttpMethod.POST, "/supplies/**").hasRole("ENCARGADO")
                                .requestMatchers(HttpMethod.GET, "/supplies/**").hasRole("ENCARGADO")
                                .requestMatchers(HttpMethod.GET, "/supplies/categories").permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/supplies/**").permitAll()

                                // Inventory
                                .requestMatchers(HttpMethod.GET, "/inventory/**").hasRole("ENCARGADO")
                                .requestMatchers(HttpMethod.POST, "/inventory/**").hasRole("ENCARGADO")

                        // entries
                        .requestMatchers(HttpMethod.POST, "/inventory/entries").hasRole("ENCARGADO")
                        .requestMatchers(HttpMethod.GET, "/inventory/entries/**").hasRole("ENCARGADO")
                        .requestMatchers(HttpMethod.GET, "/inventory/supplies-purchases").hasRole("ENCARGADO")

                        // outputs
                        .requestMatchers(HttpMethod.POST, "/inventory/outputs/**").hasRole("ENCARGADO")
                        .requestMatchers(HttpMethod.GET, "/inventory/outputs/**").hasRole("ENCARGADO")

                        // expenses
                        .requestMatchers(HttpMethod.POST, "/expenses").hasRole("ENCARGADO")
                        .requestMatchers(HttpMethod.GET, "/expenses/**").hasRole("ENCARGADO")
                        .requestMatchers(HttpMethod.GET, "/expenses/types").hasRole("ENCARGADO")

                        // reports
                        .requestMatchers(HttpMethod.GET, "/dashboard/movements/**").hasRole("ENCARGADO")
                        .requestMatchers(HttpMethod.GET, "/dashboard/expenses/**").hasRole("ENCARGADO")

                        .anyRequest().authenticated())
                .addFilter(new JwtValidationFilter(authenticationManager()))
                .csrf(config -> config.disable())
                .cors(cors -> cors.configurationSource(this.corsConfigurationSource()))
                .sessionManagement(man -> man.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build(); return build;
    }

    /**
     * Configura el origen de configuración CORS (Cross-Origin Resource Sharing) para la aplicación.
     * Permite controlar qué dominios externos pueden acceder a los recursos de tu API.
     *
     * @return CorsConfigurationSource con las políticas CORS configuradas
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config  = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST","PUT","DELETE"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Registra un filtro CORS con la máxima prioridad en la cadena de filtros.
     * Esto asegura que las políticas CORS se apliquen antes que cualquier otro filtro.
     */
    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<>
                (new CorsFilter(this.corsConfigurationSource()));
        corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return corsBean;
    }
}
