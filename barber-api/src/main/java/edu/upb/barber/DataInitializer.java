package edu.upb.barber;

import edu.upb.barber.repository.UsuarioRepository;
import edu.upb.barber.repository.entity.Usuario;
import edu.upb.barber.repository.entity.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UsuarioRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        init();
    }

    private void init() {
        if (userRepository.count() == 0) {
            Usuario root = userRepository.save(Usuario.builder()
                    .nombre("root")
                    .email("root@upb.com")
                    .rol(RolUsuario.ROLE_ADMIN_EMPRESA)
                    .apellido("Laredo")
                            .passwordHash(passwordEncoder.encode("Abc123**"))
                    .build());
        }
    }
}
