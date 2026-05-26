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
    public UserDetails loadUserByUsername(String nombre) throws UsernameNotFoundException {
        Usuario authUser = this.userRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new UsernameNotFoundException("No existe el usuario"));
        String password = authUser.getPassword();
        return new org.springframework.security.core.userdetails.User(
                authUser.getUsername(),
                password,
                authUser.isEnabled(),
                true,
                authUser.isAccountNonExpired(),
                true,
                authUser.getAuthorities()
        );
    }

}
