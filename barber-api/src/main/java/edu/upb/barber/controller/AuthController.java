package edu.upb.barber.controller;

import edu.upb.barber.config.JwtTokenProvider;
import edu.upb.barber.repository.dto.OKAuthDto;
import edu.upb.barber.repository.dto.request.AuthenticationDto;
import edu.upb.barber.repository.dto.request.RegisterRequestDto;
import edu.upb.barber.repository.entity.Usuario;
import edu.upb.barber.repository.entity.Cliente;
import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.enums.RolUsuario;
import edu.upb.barber.repository.ClienteRepository;
import edu.upb.barber.repository.UsuarioRepository;
import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.service.EmailService;
import edu.upb.barber.service.PasswordResetService;
import edu.upb.barber.service.UsuarioService;
import edu.upb.barber.service.ValidationUtils;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final JwtTokenProvider jwtTokenProvider;
    private final UsuarioService userService;
    private final AuthenticationManager authenticationManager;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordResetService passwordResetService;

    @org.springframework.beans.factory.annotation.Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;  // injected by @Value, not via constructor


    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto data) {
        log.info("Iniciando registro para el email: {}", data.email());
        try {
            if (data.email() == null || data.email().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "El email es requerido."));
            }
            if (data.password() == null || data.password().length() < 6) {
                return ResponseEntity.badRequest().body(Map.of("message", "La contrasena debe tener al menos 6 caracteres."));
            }
            if (data.confirmPassword() == null || !data.password().equals(data.confirmPassword())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Las contrasenas no coinciden."));
            }

            String email = ValidationUtils.requireText(data.email(), 120, "email").toLowerCase();
            String nombre = ValidationUtils.cleanOptionalText(data.nombre(), 80, "nombre");
            String apellido = ValidationUtils.cleanOptionalText(data.apellido(), 80, "apellido");
            String telefono = ValidationUtils.cleanOptionalText(data.telefono(), 30, "telefono");
            String documento = ValidationUtils.cleanOptionalText(data.documento(), 40, "documento");
            String notas = ValidationUtils.cleanOptionalText(data.notas(), 500, "notas");

            if (nombre == null || apellido == null || telefono == null || documento == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Nombre, apellido, telefono y documento son requeridos."));
            }
            if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                return ResponseEntity.badRequest().body(Map.of("message", "El email no tiene un formato valido."));
            }

            if (usuarioRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Ya existe un usuario con ese email."));
            }

            // Buscar empresa
            Empresa empresa = null;
            if (data.empresaId() != null && !data.empresaId().isBlank()) {
                empresa = empresaRepository.findById(data.empresaId()).orElse(null);
            }

            // Crear Usuario
            Usuario usuario = Usuario.builder()
                    .nombre(nombre != null ? nombre : email.split("@")[0])
                    .apellido(apellido != null ? apellido : "")
                    .email(email)
                    .passwordHash(passwordEncoder.encode(data.password()))
                    .rol(RolUsuario.ROLE_CLIENTE)
                    .empresa(empresa)
                    .telefono(telefono)
                    .documento(documento)
                    .activo(true)
                    .build();

            usuario = usuarioRepository.save(usuario);

            // Crear Cliente únicamente si hay una empresa asociada
            if (empresa != null) {
                Cliente cliente = new Cliente();
                String nombreCompleto = (nombre != null ? nombre : "") +
                        (apellido != null && !apellido.isBlank() ? " " + apellido : "");
                cliente.setNombre(nombreCompleto.trim());
                cliente.setEmail(email);
                cliente.setTelefono(telefono);
                cliente.setDocumento(documento);
                cliente.setNotas(notas);
                cliente.setUsuario(usuario);
                cliente.setEmpresa(empresa);
                cliente.setActivo(true);

                clienteRepository.save(cliente);
            }

            log.info("Registro exitoso para el email: {}", email);
            return ResponseEntity.ok(Map.of("message", "Cliente registrado exitosamente."));
        } catch (Exception e) {
            log.error("Error en registro de cliente: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error interno al procesar el registro."));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> token(
            @RequestBody AuthenticationDto data) {


        try {
            if (data == null || data.username() == null || data.username().isBlank()
                    || data.password() == null || data.password().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Email y contrasena son requeridos."));
            }
            OKAuthDto token = auth(data);
            return ok(token);
        } catch (BadCredentialsException e) {
            log.warn("Credenciales invalidas al autenticar: {}", data != null ? data.username() : null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Email o contrasena son incorrectos."));
        } catch (Exception e) {
            log.error("Error al autentificar el usuario: {}", data != null ? data.username() : null, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Error al autenticar");
        }
    }


    public OKAuthDto auth(AuthenticationDto data)  {
        String username = data.username();
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
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "El email es requerido."));
        }
        usuarioRepository.findByEmail(email.trim()).ifPresent(usuario -> {
            String token = passwordResetService.generarToken(usuario.getEmail());
            String link = frontendUrl + "/#reset-password?token=" + token;
            emailService.sendResetPassword(usuario.getEmail(), usuario.getNombre(), link);
        });
        // Siempre devolver OK para no exponer si el email existe
        return ResponseEntity.ok(Map.of("message", "Si el correo está registrado, recibirás un enlace para restablecer tu contraseña."));
    }

    @PostMapping("/reset-password")
    @CacheEvict(value = "usuario", allEntries = true)
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("password");
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token requerido."));
        }
        if (newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "La contraseña debe tener al menos 6 caracteres."));
        }
        String email = passwordResetService.validarToken(token);
        if (email == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "El enlace es inválido o ha expirado."));
        }
        Optional<Usuario> optUser = usuarioRepository.findByEmail(email);
        if (optUser.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Usuario no encontrado."));
        }
        Usuario usuario = optUser.get();
        usuario.setPasswordHash(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
        passwordResetService.invalidarToken(token);
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente."));
    }
}
