package edu.upb.barber;

import edu.upb.barber.repository.UsuarioRepository;
import edu.upb.barber.repository.ClienteRepository;
import edu.upb.barber.repository.EmpleadoRepository;
import edu.upb.barber.repository.EmpleadoSucursalRepository;
import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.HorarioEmpleadoRepository;
import edu.upb.barber.repository.ServicioRepository;
import edu.upb.barber.repository.SucursalRepository;
import edu.upb.barber.repository.VentaRepository;
import edu.upb.barber.repository.entity.Cliente;
import edu.upb.barber.repository.entity.Empleado;
import edu.upb.barber.repository.entity.EmpleadoSucursal;
import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.HorarioEmpleado;
import edu.upb.barber.repository.entity.Servicio;
import edu.upb.barber.repository.entity.Sucursal;
import edu.upb.barber.repository.entity.Usuario;
import edu.upb.barber.repository.entity.Venta;
import edu.upb.barber.repository.entity.enums.CargoEmpleado;
import edu.upb.barber.repository.entity.enums.CategoriaServicio;
import edu.upb.barber.repository.entity.enums.DiaSemana;
import edu.upb.barber.repository.entity.enums.RolUsuario;
import edu.upb.barber.repository.entity.enums.TipoDestinatarioServicio;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;

@Slf4j
@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UsuarioRepository userRepository;
    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoSucursalRepository empleadoSucursalRepository;
    private final HorarioEmpleadoRepository horarioEmpleadoRepository;
    private final ServicioRepository servicioRepository;
    private final EmpresaRepository empresaRepository;
    private final SucursalRepository sucursalRepository;
    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        init();
    }

    private void init() {
        if (userRepository.findByNombreIgnoreCase("root").isEmpty()) {
            userRepository.save(Usuario.builder()
                    .nombre("root")
                    .email("root@upb.com")
                    .rol(RolUsuario.ROLE_ADMIN_EMPRESA)
                    .apellido("Laredo")
                    .passwordHash(passwordEncoder.encode("Abc123**"))
                    .activo(true).build());
        }

        Usuario trabajador = userRepository.findByNombreIgnoreCase("barbero1").orElseGet(() ->
                userRepository.save(Usuario.builder()
                        .nombre("barbero1")
                        .email("barbero1@klipp.bo")
                        .rol(RolUsuario.ROLE_EMPLEADO)
                        .apellido("Demo")
                        .passwordHash(passwordEncoder.encode("Abc123**"))
                        .activo(true)
                        .build())
        );

        Empresa empresa = empresaRepository.findAll().stream().findFirst().orElseGet(() -> {
            Empresa e = new Empresa();
            e.setNombre("Barber Demo");
            e.setRazonSocial("Barber Demo SRL");
            e.setNit("1234567890");
            e.setTelefono("70000000");
            e.setEmail("demo@barber.com");
            e.setActivo(true);
            return empresaRepository.save(e);
        });

        Sucursal sucursal = sucursalRepository.findAll().stream().findFirst().orElseGet(() -> {
            Sucursal s = new Sucursal();
            s.setEmpresa(empresa);
            s.setNombre("Sucursal Central");
            s.setDireccion("Av. Principal 123");
            s.setTelefono("70000001");
            s.setActivo(true);
            return sucursalRepository.save(s);
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

        seedBarberosSucursalCentral(empresa, sucursal, trabajador);
        seedServiciosBarberia(empresa);
    }

    private void seedServiciosBarberia(Empresa empresa) {
        ServicioSeed[] servicios = {
                new ServicioSeed("Signature Fade", "Corte tecnico con perfilado y styling final.", CategoriaServicio.BARBERIA, 45, "90.00"),
                new ServicioSeed("Ritual de Barba", "Toalla caliente, navaja y acabado premium.", CategoriaServicio.BARBERIA, 30, "70.00"),
                new ServicioSeed("Corte Clasico", "Corte tradicional con lavado y peinado.", CategoriaServicio.BARBERIA, 60, "80.00"),
                new ServicioSeed("Corte y Barba", "Servicio completo de corte, barba y acabado.", CategoriaServicio.BARBERIA, 90, "140.00"),
                new ServicioSeed("Perfilado Express", "Perfilado rapido de barba y contornos.", CategoriaServicio.BARBERIA, 30, "55.00")
        };

        for (ServicioSeed seed : servicios) {
            servicioRepository.findByEmpresaIdAndNombreIgnoreCase(empresa.getId(), seed.nombre()).orElseGet(() -> {
                Servicio servicio = new Servicio();
                servicio.setEmpresa(empresa);
                servicio.setNombre(seed.nombre());
                servicio.setDescripcion(seed.descripcion());
                servicio.setCategoria(seed.categoria());
                servicio.setDestinatario(TipoDestinatarioServicio.HUMANO);
                servicio.setDuracionMinutos(seed.duracionMinutos());
                servicio.setPrecioBase(new BigDecimal(seed.precio()));
                servicio.setActivo(true);
                return servicioRepository.save(servicio);
            });
        }
    }

    private void seedBarberosSucursalCentral(Empresa empresa, Sucursal sucursal, Usuario trabajador) {
        BarberoSeed[] barberos = {
                new BarberoSeed("Carlos Mendez", "carlos.mendez@klipp.bo", "Fade y barba"),
                new BarberoSeed("Luis Rojas", "luis.rojas@klipp.bo", "Corte clasico"),
                new BarberoSeed("Marco Vega", "marco.vega@klipp.bo", "Barba y perfilado"),
                new BarberoSeed("Diego Salvatierra", "diego.salvatierra@klipp.bo", "Degrades"),
                new BarberoSeed("Andres Quiroga", "andres.quiroga@klipp.bo", "Corte premium")
        };

        for (BarberoSeed seed : barberos) {
            Empleado empleado = empleadoRepository.findByEmailIgnoreCase(seed.email()).orElseGet(() -> {
                Empleado nuevo = new Empleado();
                nuevo.setEmpresa(empresa);
                nuevo.setNombre(seed.nombre());
                nuevo.setEmail(seed.email());
                nuevo.setTelefono("70000000");
                nuevo.setCargo(CargoEmpleado.BARBERO);
                nuevo.setEspecialidad(seed.especialidad());
                nuevo.setDisponible(true);
                nuevo.setActivo(true);
                return empleadoRepository.save(nuevo);
            });

            if ("carlos.mendez@klipp.bo".equalsIgnoreCase(seed.email()) && empleado.getUsuario() == null) {
                empleado.setUsuario(trabajador);
                empleadoRepository.save(empleado);
            }

            empleadoSucursalRepository.findByEmpleadoIdAndSucursalId(empleado.getId(), sucursal.getId()).orElseGet(() -> {
                EmpleadoSucursal asignacion = new EmpleadoSucursal();
                asignacion.setEmpleado(empleado);
                asignacion.setSucursal(sucursal);
                asignacion.setActivo(true);
                return empleadoSucursalRepository.save(asignacion);
            });

            for (DiaSemana dia : DiaSemana.values()) {
                horarioEmpleadoRepository
                        .findByEmpleadoIdAndSucursalIdAndDiaSemana(empleado.getId(), sucursal.getId(), dia)
                        .orElseGet(() -> {
                            HorarioEmpleado horario = new HorarioEmpleado();
                            horario.setEmpleado(empleado);
                            horario.setSucursal(sucursal);
                            horario.setDiaSemana(dia);
                            horario.setHoraInicio(LocalTime.of(8, 0));
                            horario.setHoraFin(LocalTime.of(22, 0));
                            horario.setActivo(true);
                            return horarioEmpleadoRepository.save(horario);
                        });
            }
        }
    }

    private record BarberoSeed(String nombre, String email, String especialidad) {}

    private record ServicioSeed(
            String nombre,
            String descripcion,
            CategoriaServicio categoria,
            int duracionMinutos,
            String precio
    ) {}
}
