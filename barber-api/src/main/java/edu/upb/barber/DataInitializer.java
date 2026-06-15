package edu.upb.barber;

import edu.upb.barber.repository.*;
import edu.upb.barber.repository.entity.*;
import edu.upb.barber.repository.entity.enums.CargoEmpleado;
import edu.upb.barber.repository.entity.enums.RolUsuario;
import edu.upb.barber.repository.entity.enums.TipoEmpresa;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository    usuarioRepository;
    private final EmpresaRepository    empresaRepository;
    private final SucursalRepository   sucursalRepository;
    private final ClienteRepository    clienteRepository;
    private final EmpleadoRepository   empleadoRepository;
    private final EspecieRepository    especieRepository;
    private final RazaRepository       razaRepository;
    private final MascotaRepository    mascotaRepository;
    private final PasswordEncoder      passwordEncoder;

    @Override
    public void run(String... args) {
        init();
    }

    @Transactional
    public void init() {
        log.info("▶ DataInitializer: verificando datos de prueba...");

        // ─────────────────────────────────────────────────────
        // USUARIO SUPER ADMIN (sin empresa — acceso global)
        // ─────────────────────────────────────────────────────
        if (usuarioRepository.findByEmail("root@upb.com").isEmpty()) {
            usuarioRepository.save(Usuario.builder()
                    .nombre("root")
                    .apellido("Admin")
                    .email("root@upb.com")
                    .rol(RolUsuario.ROLE_ADMIN_EMPRESA)
                    .passwordHash(passwordEncoder.encode("Abc123**"))
                    .activo(true).build());
            log.info("  ✓ Usuario root@upb.com creado");
        }

        // ─────────────────────────────────────────────────────
        // EMPRESA 1: BARBERÍA "Klipp Barber Studio"
        // ─────────────────────────────────────────────────────
        if (empresaRepository.findAll().stream()
                .noneMatch(e -> e.getNit().equals("1000000001"))) {

            Empresa barberia = new Empresa();
            barberia.setNombre("Klipp Barber Studio");
            barberia.setRazonSocial("Klipp Barberia SRL");
            barberia.setNit("1000000001");
            barberia.setTelefono("70100001");
            barberia.setEmail("admin@klipp-barber.com");
            barberia.setTipoEmpresa(TipoEmpresa.BARBERIA);
            barberia.setActivo(true);
            empresaRepository.save(barberia);

            // Sucursal de barbería
            Sucursal sucBarber = new Sucursal();
            sucBarber.setEmpresa(barberia);
            sucBarber.setNombre("Sucursal Equipetrol");
            sucBarber.setDireccion("Av. San Martín, Equipetrol Norte");
            sucBarber.setTelefono("70100002");
            sucBarber.setActivo(true);
            sucursalRepository.save(sucBarber);

            // Admin de barbería
            Usuario adminBarber = Usuario.builder()
                    .nombre("admin_barber")
                    .apellido("Ríos")
                    .email("admin@klipp-barber.com")
                    .empresa(barberia)
                    .rol(RolUsuario.ROLE_ADMIN_EMPRESA)
                    .passwordHash(passwordEncoder.encode("Abc123**"))
                    .activo(true).build();
            usuarioRepository.save(adminBarber);

            // Recepcionista de barbería
            Usuario recepBarber = Usuario.builder()
                    .nombre("recep_barber")
                    .apellido("Gomez")
                    .email("recep@klipp-barber.com")
                    .empresa(barberia)
                    .rol(RolUsuario.ROLE_RECEPCIONISTA)
                    .passwordHash(passwordEncoder.encode("Abc123**"))
                    .activo(true).build();
            usuarioRepository.save(recepBarber);

            // Empleados barbería
            Usuario uMiguel = Usuario.builder()
                    .nombre("miguel")
                    .apellido("Flores")
                    .email("miguel@klipp-barber.com")
                    .empresa(barberia)
                    .rol(RolUsuario.ROLE_EMPLEADO)
                    .passwordHash(passwordEncoder.encode("Abc123**"))
                    .activo(true).build();
            usuarioRepository.save(uMiguel);

            Empleado eMiguel = new Empleado();
            eMiguel.setUsuario(uMiguel);
            eMiguel.setEmpresa(barberia);
            eMiguel.setNombre("Miguel Flores");
            eMiguel.setTelefono("70100010");
            eMiguel.setCargo(CargoEmpleado.BARBERO);
            eMiguel.setEspecialidad("Cortes clásicos · Skin fade · Afeitado");
            eMiguel.setFotoUrl("MF");
            eMiguel.setDisponible(true);
            eMiguel.setActivo(true);
            empleadoRepository.save(eMiguel);

            Usuario uCarlos = Usuario.builder()
                    .nombre("carlos")
                    .apellido("Mendoza")
                    .email("carlos@klipp-barber.com")
                    .empresa(barberia)
                    .rol(RolUsuario.ROLE_EMPLEADO)
                    .passwordHash(passwordEncoder.encode("Abc123**"))
                    .activo(true).build();
            usuarioRepository.save(uCarlos);

            Empleado eCarlos = new Empleado();
            eCarlos.setUsuario(uCarlos);
            eCarlos.setEmpresa(barberia);
            eCarlos.setNombre("Carlos Mendoza");
            eCarlos.setTelefono("70100011");
            eCarlos.setCargo(CargoEmpleado.BARBERO);
            eCarlos.setEspecialidad("Degradados · Diseño de barba · Color");
            eCarlos.setFotoUrl("CM");
            eCarlos.setDisponible(true);
            eCarlos.setActivo(true);
            empleadoRepository.save(eCarlos);

            // Clientes de barbería
            Cliente c1 = new Cliente();
            c1.setEmpresa(barberia);
            c1.setNombre("Juan Pérez");
            c1.setTelefono("70200001");
            c1.setEmail("juan@gmail.com");
            c1.setDocumento("10010020");
            c1.setActivo(true);
            clienteRepository.save(c1);

            Cliente c2 = new Cliente();
            c2.setEmpresa(barberia);
            c2.setNombre("Diego Soria");
            c2.setTelefono("70200002");
            c2.setEmail("diego@gmail.com");
            c2.setDocumento("10010021");
            c2.setActivo(true);
            clienteRepository.save(c2);

            log.info("  ✓ Empresa BARBERIA 'Klipp Barber Studio' creada con sucursal, usuarios y clientes");
        }

        // ─────────────────────────────────────────────────────
        // EMPRESA 2: SALÓN "Klipp Salon Lab"
        // ─────────────────────────────────────────────────────
        if (empresaRepository.findAll().stream()
                .noneMatch(e -> e.getNit().equals("1000000002"))) {

            Empresa salon = new Empresa();
            salon.setNombre("Klipp Salon Lab");
            salon.setRazonSocial("Klipp Salon de Belleza SRL");
            salon.setNit("1000000002");
            salon.setTelefono("70200001");
            salon.setEmail("admin@klipp-salon.com");
            salon.setTipoEmpresa(TipoEmpresa.SALON);
            salon.setActivo(true);
            empresaRepository.save(salon);

            // Sucursal de salón
            Sucursal sucSalon = new Sucursal();
            sucSalon.setEmpresa(salon);
            sucSalon.setNombre("Sucursal Las Palmas");
            sucSalon.setDireccion("Av. Las Palmas, 3er anillo");
            sucSalon.setTelefono("70200002");
            sucSalon.setActivo(true);
            sucursalRepository.save(sucSalon);

            // Admin de salón
            Usuario adminSalon = Usuario.builder()
                    .nombre("admin_salon")
                    .apellido("Castillo")
                    .email("admin@klipp-salon.com")
                    .empresa(salon)
                    .rol(RolUsuario.ROLE_ADMIN_EMPRESA)
                    .passwordHash(passwordEncoder.encode("Abc123**"))
                    .activo(true).build();
            usuarioRepository.save(adminSalon);

            // Recepcionista de salón
            Usuario recepSalon = Usuario.builder()
                    .nombre("recep_salon")
                    .apellido("Vega")
                    .email("recep@klipp-salon.com")
                    .empresa(salon)
                    .rol(RolUsuario.ROLE_RECEPCIONISTA)
                    .passwordHash(passwordEncoder.encode("Abc123**"))
                    .activo(true).build();
            usuarioRepository.save(recepSalon);

            // Empleadas de salón
            Usuario uLaura = Usuario.builder()
                    .nombre("laura")
                    .apellido("Castillo")
                    .email("laura@klipp-salon.com")
                    .empresa(salon)
                    .rol(RolUsuario.ROLE_EMPLEADO)
                    .passwordHash(passwordEncoder.encode("Abc123**"))
                    .activo(true).build();
            usuarioRepository.save(uLaura);

            Empleado eLaura = new Empleado();
            eLaura.setUsuario(uLaura);
            eLaura.setEmpresa(salon);
            eLaura.setNombre("Laura Castillo");
            eLaura.setTelefono("70200010");
            eLaura.setCargo(CargoEmpleado.ESTILISTA);
            eLaura.setEspecialidad("Colorimetría · Keratina · Peinados");
            eLaura.setFotoUrl("LC");
            eLaura.setDisponible(true);
            eLaura.setActivo(true);
            empleadoRepository.save(eLaura);

            Usuario uSofia = Usuario.builder()
                    .nombre("sofia")
                    .apellido("Rojas")
                    .email("sofia@klipp-salon.com")
                    .empresa(salon)
                    .rol(RolUsuario.ROLE_EMPLEADO)
                    .passwordHash(passwordEncoder.encode("Abc123**"))
                    .activo(true).build();
            usuarioRepository.save(uSofia);

            Empleado eSofia = new Empleado();
            eSofia.setUsuario(uSofia);
            eSofia.setEmpresa(salon);
            eSofia.setNombre("Sofía Rojas");
            eSofia.setTelefono("70200011");
            eSofia.setCargo(CargoEmpleado.ESTILISTA);
            eSofia.setEspecialidad("Manicure · Pedicure · Uñas acrílicas");
            eSofia.setFotoUrl("SR");
            eSofia.setDisponible(true);
            eSofia.setActivo(true);
            empleadoRepository.save(eSofia);

            // Clientas de salón
            Cliente cs1 = new Cliente();
            cs1.setEmpresa(salon);
            cs1.setNombre("María López");
            cs1.setTelefono("70300001");
            cs1.setEmail("maria@gmail.com");
            cs1.setDocumento("20010030");
            cs1.setActivo(true);
            clienteRepository.save(cs1);

            Cliente cs2 = new Cliente();
            cs2.setEmpresa(salon);
            cs2.setNombre("Valentina Cruz");
            cs2.setTelefono("70300002");
            cs2.setEmail("valen@gmail.com");
            cs2.setDocumento("20010031");
            cs2.setActivo(true);
            clienteRepository.save(cs2);

            log.info("  ✓ Empresa SALON 'Klipp Salon Lab' creada con sucursal, usuarios y clientes");
        }

        // ─────────────────────────────────────────────────────
        // EMPRESA 3: VETERINARIA "Klipp Pet Grooming"
        // ─────────────────────────────────────────────────────
        if (empresaRepository.findAll().stream()
                .noneMatch(e -> e.getNit().equals("1000000003"))) {

            Empresa vet = new Empresa();
            vet.setNombre("Klipp Pet Grooming");
            vet.setRazonSocial("Klipp Veterinaria SRL");
            vet.setNit("1000000003");
            vet.setTelefono("70300001");
            vet.setEmail("admin@klipp-vet.com");
            vet.setTipoEmpresa(TipoEmpresa.VETERINARIA);
            vet.setActivo(true);
            empresaRepository.save(vet);

            // Sucursal de veterinaria
            Sucursal sucVet = new Sucursal();
            sucVet.setEmpresa(vet);
            sucVet.setNombre("Sucursal Urubo");
            sucVet.setDireccion("Av. Urubo, Condominio Los Pinos");
            sucVet.setTelefono("70300002");
            sucVet.setActivo(true);
            sucursalRepository.save(sucVet);

            // Admin de veterinaria
            Usuario adminVet = Usuario.builder()
                    .nombre("admin_vet")
                    .apellido("Morales")
                    .email("admin@klipp-vet.com")
                    .empresa(vet)
                    .rol(RolUsuario.ROLE_ADMIN_EMPRESA)
                    .passwordHash(passwordEncoder.encode("Abc123**"))
                    .activo(true).build();
            usuarioRepository.save(adminVet);

            // Recepcionista de veterinaria
            Usuario recepVet = Usuario.builder()
                    .nombre("recep_vet")
                    .apellido("Lima")
                    .email("recep@klipp-vet.com")
                    .empresa(vet)
                    .rol(RolUsuario.ROLE_RECEPCIONISTA)
                    .passwordHash(passwordEncoder.encode("Abc123**"))
                    .activo(true).build();
            usuarioRepository.save(recepVet);

            // Empleado veterinaria
            Usuario uDra = Usuario.builder()
                    .nombre("dra_ana")
                    .apellido("Suarez")
                    .email("ana@klipp-vet.com")
                    .empresa(vet)
                    .rol(RolUsuario.ROLE_EMPLEADO)
                    .passwordHash(passwordEncoder.encode("Abc123**"))
                    .activo(true).build();
            usuarioRepository.save(uDra);

            Empleado eDra = new Empleado();
            eDra.setUsuario(uDra);
            eDra.setEmpresa(vet);
            eDra.setNombre("Dra. Ana Suárez");
            eDra.setTelefono("70300010");
            eDra.setCargo(CargoEmpleado.OTRO);
            eDra.setEspecialidad("Veterinaria general · Grooming canino");
            eDra.setFotoUrl("AS");
            eDra.setDisponible(true);
            eDra.setActivo(true);
            empleadoRepository.save(eDra);

            // Especies y razas para veterinaria
            Especie canino = new Especie();
            canino.setNombre("Canino");
            canino.setActivo(true);
            especieRepository.save(canino);

            Especie felino = new Especie();
            felino.setNombre("Felino");
            felino.setActivo(true);
            especieRepository.save(felino);

            Raza labrador = new Raza();
            labrador.setEspecie(canino);
            labrador.setNombre("Labrador Retriever");
            labrador.setActivo(true);
            razaRepository.save(labrador);

            Raza bulldog = new Raza();
            bulldog.setEspecie(canino);
            bulldog.setNombre("Bulldog Francés");
            bulldog.setActivo(true);
            razaRepository.save(bulldog);

            Raza mestizo = new Raza();
            mestizo.setEspecie(canino);
            mestizo.setNombre("Mestizo");
            mestizo.setActivo(true);
            razaRepository.save(mestizo);

            Raza persa = new Raza();
            persa.setEspecie(felino);
            persa.setNombre("Persa");
            persa.setActivo(true);
            razaRepository.save(persa);

            // Clientes veterinaria (dueños de mascotas)
            Cliente cv1 = new Cliente();
            cv1.setEmpresa(vet);
            cv1.setNombre("Roberto Vargas");
            cv1.setTelefono("70400001");
            cv1.setEmail("roberto@gmail.com");
            cv1.setDocumento("30010040");
            cv1.setActivo(true);
            clienteRepository.save(cv1);

            // Mascota del cliente
            Mascota m1 = new Mascota();
            m1.setCliente(cv1);
            m1.setRaza(labrador);
            m1.setNombre("Max");
            m1.setNotasEspeciales("Alérgico a algunos shampoos. Usar solo productos hipoalergénicos.");
            m1.setActivo(true);
            mascotaRepository.save(m1);

            Mascota m2 = new Mascota();
            m2.setCliente(cv1);
            m2.setRaza(bulldog);
            m2.setNombre("Rocky");
            m2.setNotasEspeciales("Manejo cuidadoso. Es juguetón pero puede morder.");
            m2.setActivo(true);
            mascotaRepository.save(m2);

            log.info("  ✓ Empresa VETERINARIA 'Klipp Pet Grooming' creada con sucursal, usuarios, razas y cliente");
        }

        log.info("✅ DataInitializer completado.");
    }
}
