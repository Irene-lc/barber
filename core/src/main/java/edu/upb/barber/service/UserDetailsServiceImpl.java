package edu.upb.barber.service;

import edu.upb.barber.repository.UsuarioRepository;
import edu.upb.barber.repository.entity.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UsuarioRepository userRepository;


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Intentar por email primero; si no, por nombre de usuario
        Usuario authUser = userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByNombreIgnoreCase(identifier))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No existe el usuario con email o nombre: " + identifier));

        return new org.springframework.security.core.userdetails.User(
                authUser.getEmail(),   // usamos email como principal canónico
                authUser.getPassword(),
                authUser.isEnabled(),
                true,
                authUser.isAccountNonExpired(),
                true,
                authUser.getAuthorities()
        );
    }

}
