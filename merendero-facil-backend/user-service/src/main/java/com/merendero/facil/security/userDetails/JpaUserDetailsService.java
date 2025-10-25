package com.merendero.facil.security.userDetails;

import com.merendero.facil.entities.UserEntity;
import com.merendero.facil.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio que implementa la autenticación de Spring Security.
 * Se encarga de cargar los usuarios desde la base de datos y convertirlos
 * al formato que Spring Security necesita para el proceso de login.
 */
@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

/**
 * Carga un usuario por su nombre de usuario (email en este caso) y lo convierte
 * en un objeto UserDetails que Spring Security puede usar para autenticar.
 * */
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntityOptional = this.userRepository.findByEmail(username);
        if(userEntityOptional.isEmpty()) {
            throw new UsernameNotFoundException("Credenciales inválidas");
        }
        UserEntity userEntity = userEntityOptional.get();
        List<GrantedAuthority> authorities = userEntity.getRoles().stream()
                .map( role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        return new User(userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getActive(),
                true,
                true,
                true,
                authorities);
    }
}
