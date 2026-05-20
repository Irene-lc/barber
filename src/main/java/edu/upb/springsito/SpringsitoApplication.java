package edu.upb.springsito;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import edu.upb.springsito.repository.AgendaEventoDetalleRepository;
import edu.upb.springsito.repository.AgendaEventoEmpleadoRepository;
import edu.upb.springsito.repository.AgendaEventoRepository;
import edu.upb.springsito.repository.ClienteRepository;
import edu.upb.springsito.repository.ComboServicioDetalleRepository;
import edu.upb.springsito.repository.ComboServicioRepository;
import edu.upb.springsito.repository.EmpleadoRepository;
import edu.upb.springsito.repository.EmpleadoSucursalRepository;
import edu.upb.springsito.repository.EmpresaRepository;
import edu.upb.springsito.repository.ProductoRepository;
import edu.upb.springsito.repository.ServicioRepository;
import edu.upb.springsito.repository.SucursalRepository;
import edu.upb.springsito.repository.VentaDetalleRepository;
import edu.upb.springsito.repository.VentaRepository;
import edu.upb.springsito.repository.entity.AgendaEvento;
import edu.upb.springsito.repository.entity.AgendaEventoDetalle;
import edu.upb.springsito.repository.entity.AgendaEventoEmpleado;
import edu.upb.springsito.repository.entity.Cliente;
import edu.upb.springsito.repository.entity.ComboServicio;
import edu.upb.springsito.repository.entity.ComboServicioDetalle;
import edu.upb.springsito.repository.entity.Empleado;
import edu.upb.springsito.repository.entity.EmpleadoSucursal;
import edu.upb.springsito.repository.entity.Empresa;
import edu.upb.springsito.repository.entity.Producto;
import edu.upb.springsito.repository.entity.Servicio;
import edu.upb.springsito.repository.entity.Sucursal;
import edu.upb.springsito.repository.entity.Venta;
import edu.upb.springsito.repository.entity.VentaDetalle;
import edu.upb.springsito.repository.entity.enums.EstadoEvento;
import edu.upb.springsito.repository.entity.enums.EstadoVenta;
import edu.upb.springsito.repository.entity.enums.MetodoPago;
import edu.upb.springsito.repository.entity.enums.TipoEvento;
import edu.upb.springsito.repository.entity.enums.TipoItemVenta;

@SpringBootApplication
public class SpringsitoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringsitoApplication.class, args);
        System.out.println("ALOHA MBAPPE");
    }

    @Bean
    CommandLineRunner seedData(
            EmpresaRepository empresaRepository,
            SucursalRepository sucursalRepository,
            ClienteRepository clienteRepository,
            EmpleadoRepository empleadoRepository,
            EmpleadoSucursalRepository empleadoSucursalRepository,
            ServicioRepository servicioRepository,
            ProductoRepository productoRepository,
            ComboServicioRepository comboServicioRepository,
            ComboServicioDetalleRepository comboServicioDetalleRepository,
            AgendaEventoRepository agendaEventoRepository,
            AgendaEventoDetalleRepository agendaEventoDetalleRepository,
            AgendaEventoEmpleadoRepository agendaEventoEmpleadoRepository,
            VentaRepository ventaRepository,
            VentaDetalleRepository ventaDetalleRepository) {
        return args -> seedAllTables(
                empresaRepository,
                sucursalRepository,
                clienteRepository,
                empleadoRepository,
                empleadoSucursalRepository,
                servicioRepository,
                productoRepository,
                comboServicioRepository,
                comboServicioDetalleRepository,
                agendaEventoRepository,
                agendaEventoDetalleRepository,
                agendaEventoEmpleadoRepository,
                ventaRepository,
                ventaDetalleRepository);
    }

    @Transactional
    void seedAllTables(
            EmpresaRepository empresaRepository,
            SucursalRepository sucursalRepository,
            ClienteRepository clienteRepository,
            EmpleadoRepository empleadoRepository,
            EmpleadoSucursalRepository empleadoSucursalRepository,
            ServicioRepository servicioRepository,
            ProductoRepository productoRepository,
            ComboServicioRepository comboServicioRepository,
            ComboServicioDetalleRepository comboServicioDetalleRepository,
            AgendaEventoRepository agendaEventoRepository,
            AgendaEventoDetalleRepository agendaEventoDetalleRepository,
            AgendaEventoEmpleadoRepository agendaEventoEmpleadoRepository,
            VentaRepository ventaRepository,
            VentaDetalleRepository ventaDetalleRepository) {

        if (ventaDetalleRepository.count() > 0) {
            return;
        }

        Empresa empresa = new Empresa();
        empresa.setNombre("Urban Cut Studio");
        empresa.setRazonSocial("Urban Cut Studio SRL");
        empresa.setNit("NIT-PELQ-BARB-2026");
        empresa.setTelefono("71000001");
        empresa.setEmail("admin@urbancut.bo");
        empresa.setActiva(true);
        empresa = empresaRepository.save(empresa);

        Sucursal sucursal = new Sucursal();
        sucursal.setEmpresa(empresa);
        sucursal.setNombre("Sucursal Equipetrol");
        sucursal.setDireccion("Av. San Martin #455, Equipetrol");
        sucursal.setTelefono("73000003");
        sucursal.setZonaHoraria("America/La_Paz");
        sucursal.setActiva(true);
        sucursal = sucursalRepository.save(sucursal);

        Cliente cliente = new Cliente();
        cliente.setNombre("Carlos Rojas");
        cliente.setTelefono("72000010");
        cliente.setEmail("carlos.rojas@gmail.com");
        cliente.setDocumento("CI-6543210");
        cliente = clienteRepository.save(cliente);

        Empleado empleado = new Empleado();
        empleado.setNombre("Diego Fernandez");
        empleado.setTelefono("72000002");
        empleado.setEmail("diego@urbancut.bo");
        empleado.setCargo("Barbero Senior");
        empleado.setPorcentajeComision(new BigDecimal("18.50"));
        empleado.setPagoFijoMensual(new BigDecimal("3200.00"));
        empleado.setActivo(true);
        empleado = empleadoRepository.save(empleado);

        EmpleadoSucursal empleadoSucursal = new EmpleadoSucursal();
        empleadoSucursal.setEmpleado(empleado);
        empleadoSucursal.setSucursal(sucursal);
        empleadoSucursal.setActivo(true);
        empleadoSucursalRepository.save(empleadoSucursal);

        Servicio servicio = new Servicio();
        servicio.setNombre("Corte Fade Premium");
        servicio.setDescripcion("Corte fade con asesoria de estilo y acabado profesional.");
        servicio.setDuracionMinutos(45);
        servicio.setPrecioBase(new BigDecimal("70.00"));
        servicio.setActivo(true);
        servicio = servicioRepository.save(servicio);

        Producto producto = new Producto();
        producto.setNombre("Cera Matte Profesional");
        producto.setDescripcion("Cera de fijacion media para barberia y peluqueria.");
        producto.setPrecioVenta(new BigDecimal("65.50"));
        producto.setActivo(true);
        producto = productoRepository.save(producto);

        ComboServicio combo = new ComboServicio();
        combo.setNombre("Combo Fade + Barba");
        combo.setDescripcion("Corte fade premium y perfilado de barba con toalla caliente.");
        combo.setPrecio(new BigDecimal("90.00"));
        combo.setActivo(true);
        combo = comboServicioRepository.save(combo);

        ComboServicioDetalle comboDetalle = new ComboServicioDetalle();
        comboDetalle.setCombo(combo);
        comboDetalle.setServicio(servicio);
        comboDetalle.setOrden(1);
        comboServicioDetalleRepository.save(comboDetalle);

        OffsetDateTime inicio = OffsetDateTime.now().plusDays(1).withHour(15).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime fin = inicio.plusMinutes(60);

        AgendaEvento agendaEvento = new AgendaEvento();
        agendaEvento.setCliente(cliente);
        agendaEvento.setSucursal(sucursal);
        agendaEvento.setTipoEvento(TipoEvento.CITA);
        agendaEvento.setEstado(EstadoEvento.CONFIRMADO);
        agendaEvento.setInicio(inicio);
        agendaEvento.setFin(fin);
        agendaEvento.setNotas("Cliente solicita degradado bajo y diseno de barba.");
        agendaEvento = agendaEventoRepository.save(agendaEvento);

        AgendaEventoDetalle agendaDetalle = new AgendaEventoDetalle();
        agendaDetalle.setAgendaEvento(agendaEvento);
        agendaDetalle.setCombo(combo);
        agendaDetalle.setDuracionEstimadaMin(60);
        agendaDetalle.setPrecioAcordado(new BigDecimal("90.00"));
        agendaEventoDetalleRepository.save(agendaDetalle);

        AgendaEventoEmpleado agendaEmpleado = new AgendaEventoEmpleado();
        agendaEmpleado.setAgendaEvento(agendaEvento);
        agendaEmpleado.setEmpleado(empleado);
        agendaEmpleado.setRolEnEvento("Barbero principal");
        agendaEventoEmpleadoRepository.save(agendaEmpleado);

        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setSucursal(sucursal);
        venta.setAgendaEvento(agendaEvento);
        venta.setFecha(OffsetDateTime.now());
        venta.setSubtotal(new BigDecimal("155.50"));
        venta.setDescuento(new BigDecimal("5.50"));
        venta.setTotal(new BigDecimal("150.00"));
        venta.setEstado(EstadoVenta.COBRADA);
        venta.setMetodoPago(MetodoPago.QR);
        venta = ventaRepository.save(venta);

        VentaDetalle detalleServicio = new VentaDetalle();
        detalleServicio.setVenta(venta);
        detalleServicio.setTipoItem(TipoItemVenta.COMBO);
        detalleServicio.setCombo(combo);
        detalleServicio.setEmpleado(empleado);
        detalleServicio.setDescripcion("Combo Fade + Barba");
        detalleServicio.setCantidad(new BigDecimal("1.00"));
        detalleServicio.setPrecioUnitario(new BigDecimal("90.00"));
        detalleServicio.setSubtotal(new BigDecimal("90.00"));
        detalleServicio.setComisionPorcentaje(new BigDecimal("18.50"));
        detalleServicio.setComisionMonto(new BigDecimal("16.65"));
        ventaDetalleRepository.save(detalleServicio);

        VentaDetalle detalleProducto = new VentaDetalle();
        detalleProducto.setVenta(venta);
        detalleProducto.setTipoItem(TipoItemVenta.PRODUCTO);
        detalleProducto.setProducto(producto);
        detalleProducto.setDescripcion("Cera Matte Profesional");
        detalleProducto.setCantidad(new BigDecimal("1.00"));
        detalleProducto.setPrecioUnitario(new BigDecimal("65.50"));
        detalleProducto.setSubtotal(new BigDecimal("65.50"));
        ventaDetalleRepository.save(detalleProducto);
    }
}
