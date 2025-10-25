package com.merendero.facil.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merendero.facil.dto.LoginRequestDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.merendero.facil.auth.TokenJwtConfig.*;

/**
 * Filtro personalizado para autenticación (o sea login) con JWT.
 * Se encarga de procesar las credenciales enviadas en el login
 * y generar el token de autenticación. Autentica y genera el token.
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * Intenta autenticar al usuario cuando se hace una petición de login.
     * Lee las credenciales del cuerpo de la petición (email y password)
     * y las valida con el AuthenticationManager.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        LoginRequestDto loginRequest;
        try {
            // Convierte el JSON del cuerpo de la petición a un objeto LoginRequestDto
            loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Crea un token de autenticación con las credenciales obtenidas
        // Este token será verificado por el AuthenticationManager
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        // Delega la autenticación al AuthenticationManager
        // El manager buscará el usuario, verificará la contraseña y devolverá la autenticación completa.
        //Por debajo usará el JpaUserDetailsService
        return authenticationManager.authenticate(authToken);
    }

    /**
     * Método ejecutado cuando la autenticación (o sea el login) es exitosa.
     * Genera un token JWT con los datos del usuario y lo envía en la respuesta HTTP.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain
            , Authentication authResult) throws IOException, ServletException {

        // Obtiene los datos del usuario autenticado desde el resultado de autenticación
        User userSecurity = (User) authResult.getPrincipal();
        String username = userSecurity.getUsername();

        // Obtiene los roles/permisos
        Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();

        // Prepara los claims (datos adicionales) para incluir en el token JWT
        Claims claims = Jwts.claims()
                .add("authorities", new ObjectMapper().writeValueAsString(roles))
                .add("username", username)
                .build();

        // Construye el token JWT con:
        String token = Jwts.builder()
                .subject(username) // Identificador del usuario (email)
                .claims(claims) // Roles/permisos
                .expiration(new Date(System.currentTimeMillis() + 86400000)) //es una día
                .issuedAt(new Date()) // Fecha de emisión
                .signWith(SECRET_KEY) // Firma con la clave secreta
                .compact();

        // Añade el token al header de la respuesta
        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + token);

        // Prepara el cuerpo de la respuesta (JSON)
        Map<String, String> body = new HashMap<>();
        body.put("token", token);
        body.put("username", username);
        body.put("message", "Hola has iniciado sesión con éxito");

        // Escribe la respuesta como JSON
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setContentType(CONTENT_TYPE);
        response.setStatus(200);
    }

    /**
     * Método ejecutado cuando la autenticación (o sea el login) falla.
     * Devuelve una respuesta JSON con detalles del error y código HTTP 401 (Unauthorized).
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        Map<String, String> body = new HashMap<>();
        body.put("message", "Error: username o password incorrectos");
        body.put("error", failed.getMessage());

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setContentType(CONTENT_TYPE);
        response.setStatus(401);
    }
}
