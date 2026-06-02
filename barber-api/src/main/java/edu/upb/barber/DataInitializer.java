package edu.upb.barber;

import edu.upb.barber.repository.UsuarioRepository;
import edu.upb.barber.repository.ClienteRepository;
import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.SucursalRepository;
import edu.upb.barber.repository.VentaRepository;
import edu.upb.barber.repository.entity.Cliente;
import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.Sucursal;
import edu.upb.barber.repository.entity.Usuario;
import edu.upb.barber.repository.entity.Venta;
import edu.upb.barber.repository.entity.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

@Slf4j
@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UsuarioRepository userRepository;
    private final EmpresaRepository empresaRepository;
    private final SucursalRepository sucursalRepository;
    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        init();
    }

    private void init() {
        if (userRepository.count() == 0) {
            userRepository.save(Usuario.builder()
                    .nombre("root")
                    .email("root@upb.com")
                    .rol(RolUsuario.ROLE_ADMIN_EMPRESA)
                    .apellido("Laredo")
                    .passwordHash(passwordEncoder.encode("Abc123**"))
                    .build());
        }

        Empresa empresa = empresaRepository.findAll().stream().findFirst().orElseGet(() -> {
            jdbcTemplate.update(
                    "insert into empresa (id, nombre, razon_social, nit, telefono, email, activo, activa, created_by, modified_by, created_date, modified_date, version) " +
                            "values (gen_random_uuid(), ?, ?, ?, ?, ?, true, true, 'ADMIN', 'ADMIN', now(), now(), 0)",
                    "Barber Demo", "Barber Demo SRL", "1234567890", "70000000", "demo@barber.com"
            );
            return empresaRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new IllegalStateException("No se pudo crear empresa demo"));
        });

        Sucursal sucursal = sucursalRepository.findAll().stream().findFirst().orElseGet(() -> {
            jdbcTemplate.update(
                    "insert into sucursal (id, empresa_id, nombre, direccion, telefono, zona_horaria, activo, activa, created_by, modified_by, created_date, modified_date, version) " +
                            "values (gen_random_uuid(), ?, ?, ?, ?, ?, true, true, 'ADMIN', 'ADMIN', now(), now(), 0)",
                    empresa.getId(), "Sucursal Central", "Av. Principal 123", "70000001", "America/La_Paz"
            );
            return sucursalRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new IllegalStateException("No se pudo crear sucursal demo"));
        });

        Cliente cliente = clienteRepository.findAll().stream().findFirst().orElseGet(() -> {
            Cliente c = new Cliente();
            c.setEmpresa(empresa);
            c.setNombre("Cliente Demo");
            c.setTelefono("70000002");
            c.setEmail("cliente.demo@barber.com");
            c.setDocumento("10020030");
            return clienteRepository.save(c);
        });

        if (ventaRepository.count() == 0) {
            Venta venta = new Venta();
            venta.setSucursal(sucursal);
            venta.setCliente(cliente);
            venta.setSubtotal(new BigDecimal("10.00"));
            venta.setDescuento(BigDecimal.ZERO);
            venta.setTotal(new BigDecimal("10.00"));
            venta.setNotas("Venta inicial para prueba de QR Stereum");
            ventaRepository.save(venta);
        }
    }
}
