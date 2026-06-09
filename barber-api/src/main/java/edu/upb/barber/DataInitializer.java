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
        if (userRepository.findByEmail("root@upb.com").isEmpty()) {
            userRepository.save(Usuario.builder()
                    .nombre("root")
                    .email("root@upb.com")
                    .rol(RolUsuario.ROLE_ADMIN_EMPRESA)
                    .apellido("Laredo")
                    .passwordHash(passwordEncoder.encode("Abc123**"))
                    .activo(true).build());
        }

        if (userRepository.findByEmail("recep@upb.com").isEmpty()) {
            userRepository.save(Usuario.builder()
                    .nombre("recep")
                    .email("recep@upb.com")
                    .rol(RolUsuario.ROLE_RECEPCIONISTA)
                    .apellido("Gomez")
                    .passwordHash(passwordEncoder.encode("Abc123**"))
                    .activo(true).build());
        }

        if (userRepository.findByEmail("empleado@upb.com").isEmpty()) {
            userRepository.save(Usuario.builder()
                    .nombre("empleado")
                    .email("empleado@upb.com")
                    .rol(RolUsuario.ROLE_EMPLEADO)
                    .apellido("Perez")
                    .passwordHash(passwordEncoder.encode("Abc123**"))
                    .activo(true).build());
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

        // Seeding de Servicios
        Integer numServicios = jdbcTemplate.queryForObject("select count(*) from servicio", Integer.class);
        if (numServicios != null && numServicios == 0) {
            jdbcTemplate.update(
                "insert into servicio (id, empresa_id, nombre, descripcion, destinatario, categoria, duracion_minutos, precio_base, activo, created_by, modified_by, created_date, modified_date, version) " +
                "values (gen_random_uuid(), ?, ?, ?, ?, ?, ?, ?, true, 'ADMIN', 'ADMIN', now(), now(), 0)",
                empresa.getId(), "Corte Clásico", "Corte tradicional con tijera y máquina", "HUMANO", "BARBERIA", 30, new BigDecimal("50.00")
            );
            jdbcTemplate.update(
                "insert into servicio (id, empresa_id, nombre, descripcion, destinatario, categoria, duracion_minutos, precio_base, activo, created_by, modified_by, created_date, modified_date, version) " +
                "values (gen_random_uuid(), ?, ?, ?, ?, ?, ?, ?, true, 'ADMIN', 'ADMIN', now(), now(), 0)",
                empresa.getId(), "Fade & Skin", "Degradado perfecto desde piel", "HUMANO", "BARBERIA", 40, new BigDecimal("65.00")
            );
            jdbcTemplate.update(
                "insert into servicio (id, empresa_id, nombre, descripcion, destinatario, categoria, duracion_minutos, precio_base, activo, created_by, modified_by, created_date, modified_date, version) " +
                "values (gen_random_uuid(), ?, ?, ?, ?, ?, ?, ?, true, 'ADMIN', 'ADMIN', now(), now(), 0)",
                empresa.getId(), "Corte Femenino", "Corte y estilizado para todo tipo de cabello", "HUMANO", "PELUQUERIA", 45, new BigDecimal("80.00")
            );
            jdbcTemplate.update(
                "insert into servicio (id, empresa_id, nombre, descripcion, destinatario, categoria, duracion_minutos, precio_base, activo, created_by, modified_by, created_date, modified_date, version) " +
                "values (gen_random_uuid(), ?, ?, ?, ?, ?, ?, ?, true, 'ADMIN', 'ADMIN', now(), now(), 0)",
                empresa.getId(), "Masaje Relajante", "Masaje corporal con aceites esenciales", "HUMANO", "SPA", 60, new BigDecimal("100.00")
            );
            jdbcTemplate.update(
                "insert into servicio (id, empresa_id, nombre, descripcion, destinatario, categoria, duracion_minutos, precio_base, activo, created_by, modified_by, created_date, modified_date, version) " +
                "values (gen_random_uuid(), ?, ?, ?, ?, ?, ?, ?, true, 'ADMIN', 'ADMIN', now(), now(), 0)",
                empresa.getId(), "Baño y Secado", "Baño con shampoo premium y secado profesional", "MASCOTA", "GROOMING", 45, new BigDecimal("60.00")
            );
            jdbcTemplate.update(
                "insert into servicio (id, empresa_id, nombre, descripcion, destinatario, categoria, duracion_minutos, precio_base, activo, created_by, modified_by, created_date, modified_date, version) " +
                "values (gen_random_uuid(), ?, ?, ?, ?, ?, ?, ?, true, 'ADMIN', 'ADMIN', now(), now(), 0)",
                empresa.getId(), "Corte de Pelo", "Corte estético adaptado a la raza de la mascota", "MASCOTA", "GROOMING", 50, new BigDecimal("70.00")
            );
            jdbcTemplate.update(
                "insert into servicio (id, empresa_id, nombre, descripcion, destinatario, categoria, duracion_minutos, precio_base, activo, created_by, modified_by, created_date, modified_date, version) " +
                "values (gen_random_uuid(), ?, ?, ?, ?, ?, ?, ?, true, 'ADMIN', 'ADMIN', now(), now(), 0)",
                empresa.getId(), "Spa para Mascotas", "Baño aromático, masaje, perfume", "MASCOTA", "SPA", 90, new BigDecimal("140.00")
            );
        }

        // Seeding de Empleados y su asignación a Sucursal
        Integer numEmpleados = jdbcTemplate.queryForObject("select count(*) from empleado", Integer.class);
        if (numEmpleados != null && numEmpleados == 0) {
            String emp1Id = java.util.UUID.randomUUID().toString();
            String emp2Id = java.util.UUID.randomUUID().toString();
            String emp3Id = java.util.UUID.randomUUID().toString();

            jdbcTemplate.update(
                "insert into empleado (id, empresa_id, nombre, telefono, email, cargo, especialidad, foto_url, disponible, activo, created_by, modified_by, created_date, modified_date, version) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, true, true, 'ADMIN', 'ADMIN', now(), now(), 0)",
                emp1Id, empresa.getId(), "Miguel Ríos", "70000010", "miguel@barber.com", "BARBERO", "Cortes clásicos · Afeitado", "MR"
            );
            jdbcTemplate.update(
                "insert into empleado (id, empresa_id, nombre, telefono, email, cargo, especialidad, foto_url, disponible, activo, created_by, modified_by, created_date, modified_date, version) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, true, true, 'ADMIN', 'ADMIN', now(), now(), 0)",
                emp2Id, empresa.getId(), "Laura Castillo", "70000011", "laura@barber.com", "ESTILISTA", "Colorimetría · Keratina", "LC"
            );
            jdbcTemplate.update(
                "insert into empleado (id, empresa_id, nombre, telefono, email, cargo, especialidad, foto_url, disponible, activo, created_by, modified_by, created_date, modified_date, version) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, true, true, 'ADMIN', 'ADMIN', now(), now(), 0)",
                emp3Id, empresa.getId(), "Carlos Rojas", "70000012", "carlos@barber.com", "OTRO", "Faciales · Spa", "CR"
            );

            // Asignación de empleados a sucursal
            jdbcTemplate.update(
                "insert into empleado_sucursal (id, empleado_id, sucursal_id, activo, created_by, modified_by, created_date, modified_date, version) " +
                "values (gen_random_uuid(), ?, ?, true, 'ADMIN', 'ADMIN', now(), now(), 0)",
                emp1Id, sucursal.getId()
            );
            jdbcTemplate.update(
                "insert into empleado_sucursal (id, empleado_id, sucursal_id, activo, created_by, modified_by, created_date, modified_date, version) " +
                "values (gen_random_uuid(), ?, ?, true, 'ADMIN', 'ADMIN', now(), now(), 0)",
                emp2Id, sucursal.getId()
            );
            jdbcTemplate.update(
                "insert into empleado_sucursal (id, empleado_id, sucursal_id, activo, created_by, modified_by, created_date, modified_date, version) " +
                "values (gen_random_uuid(), ?, ?, true, 'ADMIN', 'ADMIN', now(), now(), 0)",
                emp3Id, sucursal.getId()
            );
        }

        // Asegurar asignaciones de cualquier empleado existente para evitar conflictos de validación
        jdbcTemplate.update(
            "insert into empleado_sucursal (id, empleado_id, sucursal_id, activo, created_by, modified_by, created_date, modified_date, version) " +
            "select gen_random_uuid(), e.id, ?, true, 'ADMIN', 'ADMIN', now(), now(), 0 " +
            "from empleado e where e.id not in (select empleado_id from empleado_sucursal)",
            sucursal.getId()
        );
            // Usuarios faltantes



        }
}
