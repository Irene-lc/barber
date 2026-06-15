package edu.upb.barber.controller;

import edu.upb.barber.config.JwtTokenProvider;
import edu.upb.barber.repository.dto.OKAuthDto;
import edu.upb.barber.repository.dto.request.AuthenticationDto;
import edu.upb.barber.repository.entity.Usuario;
import edu.upb.barber.service.UsuarioService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final JwtTokenProvider jwtTokenProvider;
    private final UsuarioService userService;
    private final AuthenticationManager authenticationManager;


    @PostMapping("/login")
    public ResponseEntity<?> token(
            @RequestBody AuthenticationDto data) {


        try {
            OKAuthDto token = auth(data);
            return ok(token);
        } catch (BadCredentialsException e) {
            log.error("Error BadCredentialsException al autenticar", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Error al autenticar");
        } catch (Exception e) {
            log.error("Error al autentificar el usuario: {}", data.nombre(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Error al autenticar");
        }
    }


    public OKAuthDto auth(AuthenticationDto data)  {
        String username = data.nombre();
        log.info("Getting Stereum Session for username: {}", username);
        Usuario user;
        try {
            Optional<Usuario> userOptional = userService.findByUsername(username);
            if (userOptional.isEmpty()) {
                throw new BadCredentialsException("Email o contraseña son incorrectos");
            }
            user = userOptional.get();
        } catch (Exception e) {
            log.error("No se encontró el usuario " + username + " Registrado en la base de datos");
            throw new BadCredentialsException("Email o contraseña son incorrectos");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), data.password())
            );
            log.info("Autenticado correctamente");
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities())
            );
            return jwtTokenProvider.createToken(user);
        } catch (BadCredentialsException e) {
            log.error("BadCredentialsException. Causa:{} ", e.getMessage());
            throw new BadCredentialsException("Email o contraseña son incorrectos");
        } catch (AuthenticationException e) {
            log.error("AuthenticationException. Causa:{}", e.getMessage());
            throw new BadCredentialsException("Email o contraseña son incorrectos");
        } catch (Exception e) {
            log.error("Error de autentificacion: ", e);
            throw e;
        }
    }


}
