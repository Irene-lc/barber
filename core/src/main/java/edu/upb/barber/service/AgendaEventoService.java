package edu.upb.barber.service;

import edu.upb.barber.repository.*;
import edu.upb.barber.repository.dto.request.AgendaEventoCreateRequestDto;
import edu.upb.barber.repository.dto.request.AgendaEventoDetalleCreateDto;
import edu.upb.barber.repository.dto.request.AgendaEventoEmpleadoCreateDto;
import edu.upb.barber.repository.dto.response.AgendaEventoCreateResponseDto;
import edu.upb.barber.repository.dto.response.AgendaEventoResponseDto;
import edu.upb.barber.repository.entity.*;
import edu.upb.barber.repository.entity.enums.EstadoEvento;
import edu.upb.barber.repository.entity.enums.RolEmpleadoEvento;
import edu.upb.barber.repository.entity.enums.TipoEvento;
import edu.upb.barber.repository.entity.enums.DiaSemana;
import edu.upb.barber.repository.dto.request.WalkInRequestDto;
import edu.upb.barber.repository.dto.request.VentaRequestDto;
import edu.upb.barber.repository.dto.request.VentaDetalleRequestDto;
import edu.upb.barber.repository.entity.enums.TipoItemVenta;
import edu.upb.barber.repository.entity.enums.MetodoPago;
import edu.upb.barber.repository.entity.enums.EstadoPago;
import edu.upb.barber.repository.dto.request.PagoRequestDto;
import edu.upb.barber.repository.dto.response.VentaResponseDto;
import edu.upb.barber.service.exception.OperationException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class AgendaEventoService {

    private final AgendaEventoRepository agendaEventoRepository;
    private final AgendaEventoDetalleRepository agendaEventoDetalleRepository;
    private final AgendaEventoEmpleadoRepository agendaEventoEmpleadoRepository;
    private final SucursalRepository sucursalRepository;
    private final ClienteRepository clienteRepository;
    private final MascotaRepository mascotaRepository;
    private final ServicioRepository servicioRepository;
    private final ComboServicioRepository comboServicioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoSucursalRepository empleadoSucursalRepository;
    private final LogService logService;
    private final VentaService ventaService;
    private final PagoService pagoService;
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final HorarioEmpleadoRepository horarioEmpleadoRepository;
    private final HorarioEmpleadoFechaRepository horarioEmpleadoFechaRepository;

    @Transactional
    public AgendaEventoCreateResponseDto crear(AgendaEventoCreateRequestDto request) throws Exception {

        validarRequestBase(request);

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new OperationException("Sucursal no encontrada con ID: " + request.getSucursalId()));

        Cliente cliente = null;
        if (request.getClienteId() != null && !request.getClienteId().isBlank()) {
            cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new OperationException("Cliente no encontrado con ID: " + request.getClienteId()));
        }

        // Si el cliente es null y hay un usuario autenticado con Rol Cliente, resolver o crear su perfil de Cliente para esta empresa
        if (cliente == null) {
            Usuario currentUser = null;
            if (org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() != null &&
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Usuario) {
                currentUser = (Usuario) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            }

            if (currentUser != null && currentUser.getRol() == edu.upb.barber.repository.entity.enums.RolUsuario.ROLE_CLIENTE) {
                // Buscar si ya tiene un perfil de Cliente para la Empresa de la sucursal actual
                Optional<Cliente> clienteExistente = clienteRepository.findByUsuarioIdAndEmpresaId(currentUser.getId(), sucursal.getEmpresa().getId());
                if (clienteExistente.isPresent()) {
                    cliente = clienteExistente.get();
                } else {
                    // Si no tiene perfil de Cliente en esta empresa, lo creamos ahora asociado a la Empresa de la sucursal de la cita
                    cliente = new Cliente();
                    cliente.setNombre(currentUser.getNombre() + (currentUser.getApellido() != null ? " " + currentUser.getApellido() : ""));
                    cliente.setEmail(currentUser.getEmail());
                    cliente.setTelefono(currentUser.getTelefono());
                    cliente.setDocumento(currentUser.getDocumento());
                    cliente.setUsuario(currentUser);
                    cliente.setEmpresa(sucursal.getEmpresa());
                    cliente.setActivo(true);
                    cliente = clienteRepository.save(cliente);
                    log.info("Perfil de Cliente creado automáticamente para el usuario: {} en la empresa: {}", currentUser.getEmail(), sucursal.getEmpresa().getId());
                }
            }
        }

        Mascota mascota = null;
        if (request.getMascotaId() != null && !request.getMascotaId().isBlank()) {
            mascota = mascotaRepository.findById(request.getMascotaId())
                    .orElseThrow(() -> new OperationException("Mascota no encontrada con ID: " + request.getMascotaId()));
        }

        validarClienteYSucursal(cliente, sucursal);
        validarMascotaYCliente(mascota, cliente);
        validarReglasPorTipo(request);
        validarDetalles(request.getDetalles(), request.getTipoEvento(), sucursal);
        validarDuracionEvento(request);
        validarAsignaciones(request.getEmpleados(), request.getSucursalId(), request);

        AgendaEvento agendaEvento = new AgendaEvento();
        agendaEvento.setSucursal(sucursal);
        agendaEvento.setCliente(cliente);
        agendaEvento.setMascota(mascota);
        agendaEvento.setTipoEvento(request.getTipoEvento());
        agendaEvento.setEstado(EstadoEvento.PENDIENTE);
        agendaEvento.setInicio(request.getInicio());
        agendaEvento.setFin(request.getFin());
        agendaEvento.setNotas(ValidationUtils.cleanOptionalText(request.getNotas(), 1000, "notas"));

        agendaEvento = agendaEventoRepository.save(agendaEvento);

        if (request.getDetalles() != null) {
            for (AgendaEventoDetalleCreateDto detalleDto : request.getDetalles()) {
                AgendaEventoDetalle detalle = new AgendaEventoDetalle();
                detalle.setAgendaEvento(agendaEvento);
                detalle.setDuracionEstimadaMinutos(detalleDto.getDuracionEstimadaMinutos() != null ? detalleDto.getDuracionEstimadaMinutos() : 0);
                detalle.setPrecioAcordado(detalleDto.getPrecioAcordado());
                detalle.setNotas(ValidationUtils.cleanOptionalText(detalleDto.getNotas(), 500, "notas del detalle"));

                if (detalleDto.getServicioId() != null && !detalleDto.getServicioId().isBlank()) {
                    Servicio servicio = servicioRepository.findById(detalleDto.getServicioId())
                            .orElseThrow(() -> new OperationException("Servicio no encontrado con ID: " + detalleDto.getServicioId()));
                    detalle.setServicio(servicio);
                } else if (detalleDto.getComboServicioId() != null && !detalleDto.getComboServicioId().isBlank()) {
                    ComboServicio combo = comboServicioRepository.findById(detalleDto.getComboServicioId())
                            .orElseThrow(() -> new OperationException("Combo no encontrado con ID: " + detalleDto.getComboServicioId()));
                    detalle.setComboServicio(combo);
                } else if (detalleDto.getProductoId() != null && !detalleDto.getProductoId().isBlank()) {
                    Producto producto = productoRepository.findById(detalleDto.getProductoId())
                            .orElseThrow(() -> new OperationException("Producto no encontrado con ID: " + detalleDto.getProductoId()));
                    detalle.setProducto(producto);
                    detalle.setCantidad(detalleDto.getCantidad() != null ? detalleDto.getCantidad() : 1);
                    detalle.setDuracionEstimadaMinutos(0);
                }
                agendaEventoDetalleRepository.save(detalle);
            }
        }

        if (request.getEmpleados() != null) {
            for (AgendaEventoEmpleadoCreateDto empleadoDto : request.getEmpleados()) {
                Empleado empleado = empleadoRepository.findById(empleadoDto.getEmpleadoId())
                        .orElseThrow(() -> new OperationException("Empleado no encontrado con ID: " + empleadoDto.getEmpleadoId()));

                AgendaEventoEmpleado agendaEventoEmpleado = new AgendaEventoEmpleado();
                agendaEventoEmpleado.setAgendaEvento(agendaEvento);
                agendaEventoEmpleado.setEmpleado(empleado);
                agendaEventoEmpleado.setRolEnEvento(
                        empleadoDto.getRolEnEvento() == null ? RolEmpleadoEvento.RESPONSABLE : empleadoDto.getRolEnEvento()
                );
                agendaEventoEmpleadoRepository.save(agendaEventoEmpleado);
            }
        }

        logService.info("AgendaEvento creado exitosamente: " + agendaEvento.getId());
        AgendaEventoCreateResponseDto response = new AgendaEventoCreateResponseDto();
        response.setAgendaEventoId(agendaEvento.getId());
        response.setEstado(agendaEvento.getEstado());
        return response;
    }




    private void validarRequestBase(AgendaEventoCreateRequestDto request) throws Exception {
        if (request == null) {
            logService.error("Error en AgendaEvento. Request no puede ser null");
            throw new OperationException("Request no puede ser null");
        }
        if (request.getSucursalId() == null || request.getSucursalId().isBlank()) {
            log.error("Error en AgendaEvento. sucursal_id es requerido");
            logService.error("Error en AgendaEvento. sucursal_id es requerido");
            throw new OperationException("sucursal_id es requerido");
        }
        if (request.getTipoEvento() == null) {
            log.error("Error en AgendaEvento. tipo_evento es requerido");
            logService.error("Error en AgendaEvento. tipo_evento es requerido");
            throw new OperationException("tipo_evento es requerido");
        }
        if (request.getInicio() == null || request.getFin() == null) {
            log.error("Error en AgendaEvento. inicio y fin son requeridos");
            logService.error("Error en AgendaEvento. inicio y fin son requeridos");
            throw new OperationException("inicio y fin son requeridos");
        }
        if (!request.getInicio().isBefore(request.getFin())) {
            log.error("Error en AgendaEvento. inicio debe ser menor que fin");
            logService.error("Error en AgendaEvento. inicio debe ser menor que fin");
            throw new OperationException("inicio debe ser menor que fin");
        }
        if (!request.getInicio().isAfter(OffsetDateTime.now())) {
            log.error("Error en AgendaEvento. No se puede agendar en fecha u hora pasada");
            logService.error("Error en AgendaEvento. No se puede agendar en fecha u hora pasada");
            throw new OperationException("No se puede agendar una cita en una fecha u hora pasada");
        }
        ZoneId zone = ZoneId.systemDefault();
        LocalDate fechaInicio = request.getInicio().atZoneSameInstant(zone).toLocalDate();
        LocalDate fechaFin = request.getFin().atZoneSameInstant(zone).toLocalDate();
        if (!fechaInicio.equals(fechaFin)) {
            throw new OperationException("La cita debe iniciar y terminar el mismo dia");
        }
    }

    private void validarReglasPorTipo(AgendaEventoCreateRequestDto request) throws Exception {
        if (request.getTipoEvento() == TipoEvento.CITA) {
            if (request.getEmpleados() == null || request.getEmpleados().isEmpty()) {
                log.error("Error en AgendaEvento. Para tipo CITA, empleados es requerido");
                logService.error("Error en AgendaEvento. Para tipo CITA, empleados es requerido");
                throw new OperationException("Para tipo CITA, empleados es requerido");
            }
            if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
                log.error("Error en AgendaEvento. Para tipo CITA, detalles es requerido");
                logService.error("Error en AgendaEvento. Para tipo CITA, detalles es requerido");
                throw new OperationException("Para tipo CITA, detalles es requerido");
            }
        } else {
            if ((request.getTipoEvento() == TipoEvento.BLOQUEO || request.getTipoEvento() == TipoEvento.DESCANSO || request.getTipoEvento() == TipoEvento.CAPACITACION)
                    && (request.getEmpleados() == null || request.getEmpleados().isEmpty())) {
                throw new OperationException("Para bloqueos o eventos internos, empleados es requerido");
            }
        }
        if (request.getTipoEvento() != TipoEvento.CITA && request.getDetalles() != null && !request.getDetalles().isEmpty()) {
            log.error("Error en AgendaEvento. detalles solo aplica para tipo CITA");
            logService.error("Error en AgendaEvento. detalles solo aplica para tipo CITA");
            throw new OperationException("detalles solo aplica para tipo CITA");
        }
    }

    private void validarDetalles(List<AgendaEventoDetalleCreateDto> detalles, TipoEvento tipoEvento, Sucursal sucursal) throws Exception {
        if (detalles == null) {
            return;
        }
        int duracionTotal = 0;
        for (AgendaEventoDetalleCreateDto detalle : detalles) {
            boolean tieneServicio = detalle.getServicioId() != null && !detalle.getServicioId().isBlank();
            boolean tieneCombo = detalle.getComboServicioId() != null && !detalle.getComboServicioId().isBlank();
            boolean tieneProducto = detalle.getProductoId() != null && !detalle.getProductoId().isBlank();

            int count = (tieneServicio ? 1 : 0) + (tieneCombo ? 1 : 0) + (tieneProducto ? 1 : 0);
            if (count != 1) {
                log.error("Error en detalle AgendaEvento. Debe tener exactamente uno de: servicio_id, combo_servicio_id o producto_id");
                logService.error("Error en detalle AgendaEvento. Debe tener exactamente uno de: servicio_id, combo_servicio_id o producto_id");
                throw new OperationException("Cada detalle debe tener exactamente uno de: servicio_id, combo_servicio_id o producto_id");
            }
            if (tieneServicio) {
                Servicio servicio = servicioRepository.findById(detalle.getServicioId())
                        .orElseThrow(() -> new OperationException("Servicio no encontrado con ID: " + detalle.getServicioId()));
                validarEmpresaItem(servicio.getEmpresa(), sucursal, "servicio");
                if (!servicio.isActivo()) {
                    throw new OperationException("El servicio seleccionado no esta activo");
                }
                detalle.setDuracionEstimadaMinutos(servicio.getDuracionMinutos());
                detalle.setPrecioAcordado(servicio.getPrecioBase());
                duracionTotal += servicio.getDuracionMinutos();
            } else if (tieneCombo) {
                ComboServicio combo = comboServicioRepository.findById(detalle.getComboServicioId())
                        .orElseThrow(() -> new OperationException("Combo no encontrado con ID: " + detalle.getComboServicioId()));
                validarEmpresaItem(combo.getEmpresa(), sucursal, "combo");
                if (!combo.isActivo()) {
                    throw new OperationException("El combo seleccionado no esta activo");
                }
                detalle.setDuracionEstimadaMinutos(combo.getDuracionMinutos());
                detalle.setPrecioAcordado(combo.getPrecio());
                duracionTotal += combo.getDuracionMinutos();
            } else {
                Producto producto = productoRepository.findById(detalle.getProductoId())
                        .orElseThrow(() -> new OperationException("Producto no encontrado con ID: " + detalle.getProductoId()));
                validarEmpresaItem(producto.getEmpresa(), sucursal, "producto");
                if (!producto.isActivo()) {
                    throw new OperationException("El producto seleccionado no esta activo");
                }
                int cantidad = detalle.getCantidad() != null ? detalle.getCantidad() : 1;
                if (cantidad <= 0) {
                    throw new OperationException("La cantidad del producto debe ser mayor que cero");
                }
                detalle.setCantidad(cantidad);
                detalle.setDuracionEstimadaMinutos(0);
                detalle.setPrecioAcordado(producto.getPrecioVenta().multiply(BigDecimal.valueOf(cantidad)));
            }
            if (detalle.getNotas() != null) {
                detalle.setNotas(ValidationUtils.cleanOptionalText(detalle.getNotas(), 500, "notas del detalle"));
            }
        }
        if (tipoEvento == TipoEvento.CITA && duracionTotal <= 0) {
            throw new OperationException("La cita debe incluir al menos un servicio o combo con duracion");
        }
    }

    private void validarAsignaciones(
            List<AgendaEventoEmpleadoCreateDto> empleados,
            String sucursalId,
            AgendaEventoCreateRequestDto request
    ) throws Exception {
        if (empleados == null || empleados.isEmpty()) {
            return;
        }

        Set<String> ids = new HashSet<>();
        for (AgendaEventoEmpleadoCreateDto asignacion : empleados) {
            if (asignacion.getEmpleadoId() == null || asignacion.getEmpleadoId().isBlank()) {
                log.error("Error en asignacion AgendaEvento. empleado_id es requerido");
                logService.error("Error en asignacion AgendaEvento. empleado_id es requerido en cada asignacion");
                throw new OperationException("empleado_id es requerido en cada asignacion");
            }
            if (!ids.add(asignacion.getEmpleadoId())) {
                log.error("Error en asignacion AgendaEvento. Empleado duplicado: {}", asignacion.getEmpleadoId());
                logService.error("Error en asignacion AgendaEvento. Empleado duplicado: " + asignacion.getEmpleadoId());
                throw new OperationException("Empleado duplicado en asignaciones: " + asignacion.getEmpleadoId());
            }

            Empleado empleado = empleadoRepository.findById(asignacion.getEmpleadoId())
                    .orElseThrow(() -> new OperationException("Empleado no encontrado con ID: " + asignacion.getEmpleadoId()));
            if (!empleado.isActivo()) {
                log.error("Error en asignacion AgendaEvento. Empleado inactivo: {}", empleado.getId());
                logService.error("Error en asignacion AgendaEvento. Empleado inactivo: " + empleado.getId());
                throw new OperationException("Empleado inactivo: " + empleado.getId());
            }

            boolean pertenece = empleadoSucursalRepository
                    .existsByEmpleadoIdAndSucursalIdAndActivoTrue(asignacion.getEmpleadoId(), sucursalId);
            if (!pertenece) {
                log.error("Error en asignacion AgendaEvento. Empleado {} no pertenece a sucursal {}", asignacion.getEmpleadoId(), sucursalId);
                logService.error("Error en asignacion AgendaEvento. Empleado " + asignacion.getEmpleadoId() + " no pertenece a la sucursal " + sucursalId);
                throw new OperationException("Empleado " + asignacion.getEmpleadoId() + " no pertenece a la sucursal " + sucursalId);
            }
            validarHorarioEmpleado(asignacion.getEmpleadoId(), sucursalId, request.getInicio(), request.getFin());

            boolean enConflicto = agendaEventoEmpleadoRepository.existsConflictoHorarioEmpleado(
                    asignacion.getEmpleadoId(),
                    request.getInicio(),
                    request.getFin(),
                    Arrays.asList(EstadoEvento.CANCELADO, EstadoEvento.NO_SHOW)
            );
            if (enConflicto) {
                log.error("Error en asignacion AgendaEvento. Conflicto de horario para empleado: {}", asignacion.getEmpleadoId());
                logService.error("Error en asignacion AgendaEvento. Conflicto de horario para empleado: " + asignacion.getEmpleadoId());
                throw new OperationException("Conflicto de horario para empleado: " + asignacion.getEmpleadoId());
            }
        }
    }

    private void validarClienteYSucursal(Cliente cliente, Sucursal sucursal) throws OperationException {
        if (cliente != null && cliente.getEmpresa() != null && sucursal.getEmpresa() != null
                && !cliente.getEmpresa().getId().equals(sucursal.getEmpresa().getId())) {
            throw new OperationException("El cliente no pertenece a la empresa de la sucursal seleccionada");
        }
        if (!sucursal.isActivo()) {
            throw new OperationException("La sucursal seleccionada no esta activa");
        }
    }

    private void validarMascotaYCliente(Mascota mascota, Cliente cliente) throws OperationException {
        if (mascota == null) {
            return;
        }
        if (!mascota.isActivo()) {
            throw new OperationException("La mascota seleccionada no esta activa");
        }
        if (cliente == null || mascota.getCliente() == null || !mascota.getCliente().getId().equals(cliente.getId())) {
            throw new OperationException("La mascota seleccionada no pertenece al cliente de la cita");
        }
    }

    private void validarEmpresaItem(Empresa empresaItem, Sucursal sucursal, String tipo) throws OperationException {
        if (empresaItem == null || sucursal.getEmpresa() == null || !empresaItem.getId().equals(sucursal.getEmpresa().getId())) {
            throw new OperationException("El " + tipo + " seleccionado no pertenece a la empresa de la sucursal");
        }
    }

    private void validarDuracionEvento(AgendaEventoCreateRequestDto request) throws OperationException {
        if (request.getTipoEvento() != TipoEvento.CITA || request.getDetalles() == null) {
            return;
        }
        int duracionDetalles = request.getDetalles().stream()
                .map(AgendaEventoDetalleCreateDto::getDuracionEstimadaMinutos)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
        long duracionEvento = Duration.between(request.getInicio(), request.getFin()).toMinutes();
        if (duracionEvento != duracionDetalles) {
            throw new OperationException("La duracion de la cita no coincide con la duracion real de los servicios");
        }
    }

    private void validarHorarioEmpleado(String empleadoId, String sucursalId, OffsetDateTime inicio, OffsetDateTime fin) throws OperationException {
        ZoneId zone = ZoneId.systemDefault();
        LocalDate fecha = inicio.atZoneSameInstant(zone).toLocalDate();
        LocalTime horaInicio = inicio.atZoneSameInstant(zone).toLocalTime();
        LocalTime horaFin = fin.atZoneSameInstant(zone).toLocalTime();

        List<HorarioEmpleadoFecha> horariosFecha = horarioEmpleadoFechaRepository
                .findByEmpleadoIdAndSucursalIdAndFechaAndActivoTrue(empleadoId, sucursalId, fecha);

        boolean dentroHorario;
        if (!horariosFecha.isEmpty()) {
            dentroHorario = horariosFecha.stream()
                    .anyMatch(h -> !horaInicio.isBefore(h.getHoraInicio()) && !horaFin.isAfter(h.getHoraFin()));
        } else {
            DiaSemana dia = mapDiaSemana(inicio.atZoneSameInstant(zone).getDayOfWeek());
            dentroHorario = horarioEmpleadoRepository
                    .findByEmpleadoIdAndSucursalIdAndActivoTrue(empleadoId, sucursalId)
                    .stream()
                    .filter(h -> h.getDiaSemana() == dia)
                    .anyMatch(h -> !horaInicio.isBefore(h.getHoraInicio()) && !horaFin.isAfter(h.getHoraFin()));
        }

        if (!dentroHorario) {
            throw new OperationException("La cita esta fuera del horario laboral del empleado seleccionado");
        }
    }

    private DiaSemana mapDiaSemana(java.time.DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> DiaSemana.LUNES;
            case TUESDAY -> DiaSemana.MARTES;
            case WEDNESDAY -> DiaSemana.MIERCOLES;
            case THURSDAY -> DiaSemana.JUEVES;
            case FRIDAY -> DiaSemana.VIERNES;
            case SATURDAY -> DiaSemana.SABADO;
            case SUNDAY -> DiaSemana.DOMINGO;
        };
    }

    @Transactional(readOnly = true)
    public List<AgendaEventoResponseDto> listar() {
        edu.upb.barber.repository.entity.Usuario currentUser = null;
        if (org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() != null &&
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof edu.upb.barber.repository.entity.Usuario) {
            currentUser = (edu.upb.barber.repository.entity.Usuario) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }

        List<AgendaEvento> eventos;
        if (currentUser != null && currentUser.getRol() == edu.upb.barber.repository.entity.enums.RolUsuario.ROLE_CLIENTE) {
            List<String> clienteIds = clienteRepository.findByUsuarioId(currentUser.getId()).stream()
                    .map(Cliente::getId)
                    .toList();
            if (clienteIds.isEmpty()) {
                eventos = List.of();
            } else {
                eventos = agendaEventoRepository.findByClienteIdIn(clienteIds);
            }
        } else if (currentUser != null && currentUser.getEmpresa() != null) {
            eventos = agendaEventoRepository.findBySucursal_Empresa(currentUser.getEmpresa());
        } else {
            eventos = agendaEventoRepository.findAll();
        }

        return mapEventosToResponse(eventos);
    }

    @Transactional(readOnly = true)
    public Page<AgendaEventoResponseDto> listarPaginado(Pageable pageable) {
        edu.upb.barber.repository.entity.Usuario currentUser = null;
        if (org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() != null &&
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof edu.upb.barber.repository.entity.Usuario) {
            currentUser = (edu.upb.barber.repository.entity.Usuario) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }

        Page<AgendaEvento> eventos;
        if (currentUser != null && currentUser.getRol() == edu.upb.barber.repository.entity.enums.RolUsuario.ROLE_CLIENTE) {
            List<String> clienteIds = clienteRepository.findByUsuarioId(currentUser.getId()).stream()
                    .map(Cliente::getId)
                    .toList();
            if (clienteIds.isEmpty()) {
                return new PageImpl<>(List.of(), pageable, 0);
            }
            eventos = agendaEventoRepository.findByClienteIdIn(clienteIds, pageable);
        } else if (currentUser != null && currentUser.getEmpresa() != null) {
            eventos = agendaEventoRepository.findBySucursal_Empresa(currentUser.getEmpresa(), pageable);
        } else {
            eventos = agendaEventoRepository.findAll(pageable);
        }

        return new PageImpl<>(mapEventosToResponse(eventos.getContent()), pageable, eventos.getTotalElements());
    }



@Transactional(readOnly = true)
    public List<AgendaEventoResponseDto> listarPorSucursal(String sucursalId,Pageable pageable) {
      
        Page<AgendaEvento> eventos = agendaEventoRepository.findBySucursalId(sucursalId, pageable);
        return mapEventosToResponse(eventos.getContent());

}


   

    @Transactional(readOnly = true)
    public Optional<AgendaEventoResponseDto> findById(String id) {
        return agendaEventoRepository.findById(id)
                .map(ae -> {
                    AgendaEventoResponseDto dto = new AgendaEventoResponseDto(ae);
                    List<AgendaEventoResponseDto.DetalleDto> detalles = agendaEventoDetalleRepository.findByAgendaEventoId(ae.getId()).stream()
                            .map(this::mapDetalleToDto) //
                                    /* d.getServicio() != null ? d.getServicio().getId() : null,
                                    d.getServicio() != null ? d.getServicio().getNombre() : "Combo",
                                    d.getPrecioAcordado() != null ? d.getPrecioAcordado().doubleValue() : 0.0,
                                    d.getDuracionEstimadaMinutos()
                            */
                            .toList();
                    dto.setDetalles(detalles);

                    List<AgendaEventoResponseDto.EmpleadoDto> empleados = agendaEventoEmpleadoRepository.findByAgendaEventoId(ae.getId()).stream()
                            .map(e -> new AgendaEventoResponseDto.EmpleadoDto(
                                    e.getEmpleado().getId(),
                                    e.getEmpleado().getNombre(),
                                    e.getRolEnEvento() != null ? e.getRolEnEvento().name() : null
                            ))
                            .toList();
                    dto.setEmpleados(empleados);

                    return dto;
                });
    }

 @ Transactional(readOnly = true)
    public Optional<AgendaEventoResponseDto> findByIdSucursalPaginado(String id, Pageable pageable){
        
        return agendaEventoRepository.findById(id)
                .map(ae -> {
                    AgendaEventoResponseDto dto = new AgendaEventoResponseDto(ae);
                    List<AgendaEventoResponseDto.DetalleDto> detalles = agendaEventoDetalleRepository.findByAgendaEventoId(ae.getId()).stream()
                            .map(this::mapDetalleToDto)
                            .toList();
                    dto.setDetalles(detalles);

                    List<AgendaEventoResponseDto.EmpleadoDto> empleados = agendaEventoEmpleadoRepository.findByAgendaEventoId(ae.getId()).stream()
                            .map(e -> new AgendaEventoResponseDto.EmpleadoDto(
                                    e.getEmpleado().getId(),
                                    e.getEmpleado().getNombre(),
                                    e.getRolEnEvento() != null ? e.getRolEnEvento().name() : null
                            ))
                            .toList();
                    dto.setEmpleados(empleados);

                    return dto;
                });
    }
    



    

    @Transactional
    public AgendaEventoCreateResponseDto update(String id, AgendaEventoCreateRequestDto request) throws Exception {
        AgendaEvento agendaEvento = agendaEventoRepository.findById(id)
                .orElseThrow(() -> new OperationException("AgendaEvento no encontrado con ID: " + id));

        validarRequestBase(request);

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new OperationException("Sucursal no encontrada con ID: " + request.getSucursalId()));

        Cliente cliente = null;
        if (request.getClienteId() != null && !request.getClienteId().isBlank()) {
            cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new OperationException("Cliente no encontrado con ID: " + request.getClienteId()));
        }

        Mascota mascota = null;
        if (request.getMascotaId() != null && !request.getMascotaId().isBlank()) {
            mascota = mascotaRepository.findById(request.getMascotaId())
                    .orElseThrow(() -> new OperationException("Mascota no encontrada con ID: " + request.getMascotaId()));
        }

        validarClienteYSucursal(cliente, sucursal);
        validarMascotaYCliente(mascota, cliente);
        validarReglasPorTipo(request);
        validarDetalles(request.getDetalles(), request.getTipoEvento(), sucursal);
        validarDuracionEvento(request);
        validarAsignacionesParaUpdate(request.getEmpleados(), request.getSucursalId(), request, id);

        agendaEventoDetalleRepository.deleteByAgendaEventoId(id);
        agendaEventoEmpleadoRepository.deleteByAgendaEventoId(id);

        agendaEvento.setSucursal(sucursal);
        agendaEvento.setCliente(cliente);
        agendaEvento.setMascota(mascota);
        agendaEvento.setTipoEvento(request.getTipoEvento());
        agendaEvento.setInicio(request.getInicio());
        agendaEvento.setFin(request.getFin());
        agendaEvento.setNotas(ValidationUtils.cleanOptionalText(request.getNotas(), 1000, "notas"));

        agendaEvento = agendaEventoRepository.save(agendaEvento);

        if (request.getDetalles() != null) {
            for (AgendaEventoDetalleCreateDto detalleDto : request.getDetalles()) {
                AgendaEventoDetalle detalle = new AgendaEventoDetalle();
                detalle.setAgendaEvento(agendaEvento);
                detalle.setDuracionEstimadaMinutos(detalleDto.getDuracionEstimadaMinutos() != null ? detalleDto.getDuracionEstimadaMinutos() : 0);
                detalle.setPrecioAcordado(detalleDto.getPrecioAcordado());
                detalle.setNotas(ValidationUtils.cleanOptionalText(detalleDto.getNotas(), 500, "notas del detalle"));

                if (detalleDto.getServicioId() != null && !detalleDto.getServicioId().isBlank()) {
                    Servicio servicio = servicioRepository.findById(detalleDto.getServicioId())
                            .orElseThrow(() -> new OperationException("Servicio no encontrado con ID: " + detalleDto.getServicioId()));
                    detalle.setServicio(servicio);
                } else if (detalleDto.getComboServicioId() != null && !detalleDto.getComboServicioId().isBlank()) {
                    ComboServicio combo = comboServicioRepository.findById(detalleDto.getComboServicioId())
                            .orElseThrow(() -> new OperationException("Combo no encontrado con ID: " + detalleDto.getComboServicioId()));
                    detalle.setComboServicio(combo);
                } else if (detalleDto.getProductoId() != null && !detalleDto.getProductoId().isBlank()) {
                    Producto producto = productoRepository.findById(detalleDto.getProductoId())
                            .orElseThrow(() -> new OperationException("Producto no encontrado con ID: " + detalleDto.getProductoId()));
                    detalle.setProducto(producto);
                    detalle.setCantidad(detalleDto.getCantidad() != null ? detalleDto.getCantidad() : 1);
                    detalle.setDuracionEstimadaMinutos(0);
                }
                agendaEventoDetalleRepository.save(detalle);
            }
        }

        if (request.getEmpleados() != null) {
            for (AgendaEventoEmpleadoCreateDto empleadoDto : request.getEmpleados()) {
                Empleado empleado = empleadoRepository.findById(empleadoDto.getEmpleadoId())
                        .orElseThrow(() -> new OperationException("Empleado no encontrado con ID: " + empleadoDto.getEmpleadoId()));

                AgendaEventoEmpleado agendaEventoEmpleado = new AgendaEventoEmpleado();
                agendaEventoEmpleado.setAgendaEvento(agendaEvento);
                agendaEventoEmpleado.setEmpleado(empleado);
                agendaEventoEmpleado.setRolEnEvento(
                        empleadoDto.getRolEnEvento() == null ? RolEmpleadoEvento.RESPONSABLE : empleadoDto.getRolEnEvento()
                );
                agendaEventoEmpleadoRepository.save(agendaEventoEmpleado);
            }
        }

        logService.info("AgendaEvento actualizado exitosamente: " + id);
        AgendaEventoCreateResponseDto response = new AgendaEventoCreateResponseDto();
        response.setAgendaEventoId(agendaEvento.getId());
        response.setEstado(agendaEvento.getEstado());
        return response;
    }

    @Transactional
    public void delete(String id) throws Exception {
        AgendaEvento agendaEvento = agendaEventoRepository.findById(id)
                .orElseThrow(() -> new OperationException("AgendaEvento no encontrado con ID: " + id));

        agendaEventoDetalleRepository.deleteByAgendaEventoId(id);
        agendaEventoEmpleadoRepository.deleteByAgendaEventoId(id);
        agendaEventoRepository.delete(agendaEvento);
        logService.info("AgendaEvento eliminado exitosamente: " + id);
    }

    @Transactional
    public AgendaEventoResponseDto actualizarEstado(String id, EstadoEvento nuevoEstado) throws Exception {
        AgendaEvento agendaEvento = agendaEventoRepository.findById(id)
                .orElseThrow(() -> new OperationException("AgendaEvento no encontrado con ID: " + id));
        validarCambioEstado(agendaEvento, nuevoEstado);
        agendaEvento.setEstado(nuevoEstado);
        agendaEvento = agendaEventoRepository.save(agendaEvento);
        logService.info("Estado de AgendaEvento actualizado a " + nuevoEstado + " para id: " + id);
        return new AgendaEventoResponseDto(agendaEvento);
    }

    private void validarCambioEstado(AgendaEvento agendaEvento, EstadoEvento nuevoEstado) throws OperationException {
        if (nuevoEstado == null) {
            throw new OperationException("nuevoEstado es requerido");
        }
        EstadoEvento actual = agendaEvento.getEstado();
        if (actual == EstadoEvento.FINALIZADO || actual == EstadoEvento.CANCELADO || actual == EstadoEvento.NO_SHOW) {
            throw new OperationException("No se puede cambiar el estado de una cita en estado final");
        }
        if (nuevoEstado == EstadoEvento.PENDIENTE && actual != EstadoEvento.PENDIENTE) {
            throw new OperationException("No se puede volver una cita a PENDIENTE");
        }
        if (nuevoEstado == EstadoEvento.CANCELADO && agendaEvento.getInicio() != null
                && !agendaEvento.getInicio().isAfter(OffsetDateTime.now())) {
            throw new OperationException("No se puede cancelar una cita pasada o ya iniciada");
        }
        boolean permitido = switch (actual) {
            case PENDIENTE -> nuevoEstado == EstadoEvento.CONFIRMADO || nuevoEstado == EstadoEvento.CANCELADO || nuevoEstado == EstadoEvento.NO_SHOW;
            case CONFIRMADO -> nuevoEstado == EstadoEvento.EN_PROCESO || nuevoEstado == EstadoEvento.CANCELADO || nuevoEstado == EstadoEvento.NO_SHOW;
            case EN_PROCESO -> nuevoEstado == EstadoEvento.FINALIZADO || nuevoEstado == EstadoEvento.NO_SHOW;
            default -> false;
        };
        if (!permitido && actual != nuevoEstado) {
            throw new OperationException("Transicion de estado no permitida: " + actual + " -> " + nuevoEstado);
        }
    }

    private void validarAsignacionesParaUpdate(
            List<AgendaEventoEmpleadoCreateDto> empleados,
            String sucursalId,
            AgendaEventoCreateRequestDto request,
            String excludeEventoId
    ) throws Exception {
        if (empleados == null || empleados.isEmpty()) {
            return;
        }

        Set<String> ids = new HashSet<>();
        for (AgendaEventoEmpleadoCreateDto asignacion : empleados) {
            if (asignacion.getEmpleadoId() == null || asignacion.getEmpleadoId().isBlank()) {
                log.error("Error en asignacion AgendaEvento. empleado_id es requerido");
                logService.error("Error en asignacion AgendaEvento. empleado_id es requerido en cada asignacion");
                throw new OperationException("empleado_id es requerido en cada asignacion");
            }
            if (!ids.add(asignacion.getEmpleadoId())) {
                log.error("Error en asignacion AgendaEvento. Empleado duplicado: {}", asignacion.getEmpleadoId());
                logService.error("Error en asignacion AgendaEvento. Empleado duplicado: " + asignacion.getEmpleadoId());
                throw new OperationException("Empleado duplicado en asignaciones: " + asignacion.getEmpleadoId());
            }

            Empleado empleado = empleadoRepository.findById(asignacion.getEmpleadoId())
                    .orElseThrow(() -> new OperationException("Empleado no encontrado con ID: " + asignacion.getEmpleadoId()));
            if (!empleado.isActivo()) {
                log.error("Error en asignacion AgendaEvento. Empleado inactivo: {}", empleado.getId());
                logService.error("Error en asignacion AgendaEvento. Empleado inactivo: " + empleado.getId());
                throw new OperationException("Empleado inactivo: " + empleado.getId());
            }

            boolean pertenece = empleadoSucursalRepository
                    .existsByEmpleadoIdAndSucursalIdAndActivoTrue(asignacion.getEmpleadoId(), sucursalId);
            if (!pertenece) {
                log.error("Error en asignacion AgendaEvento. Empleado {} no pertenece a sucursal {}", asignacion.getEmpleadoId(), sucursalId);
                logService.error("Error en asignacion AgendaEvento. Empleado " + asignacion.getEmpleadoId() + " no pertenece a la sucursal " + sucursalId);
                throw new OperationException("Empleado " + asignacion.getEmpleadoId() + " no pertenece a la sucursal " + sucursalId);
            }
            validarHorarioEmpleado(asignacion.getEmpleadoId(), sucursalId, request.getInicio(), request.getFin());

            boolean enConflicto = agendaEventoEmpleadoRepository.existsConflictoHorarioEmpleadoExcludingEvent(
                    asignacion.getEmpleadoId(),
                    excludeEventoId,
                    request.getInicio(),
                    request.getFin(),
                    Arrays.asList(EstadoEvento.CANCELADO, EstadoEvento.NO_SHOW)
            );
            if (enConflicto) {
                log.error("Error en asignacion AgendaEvento. Conflicto de horario para empleado: {}", asignacion.getEmpleadoId());
                logService.error("Error en asignacion AgendaEvento. Conflicto de horario para empleado: " + asignacion.getEmpleadoId());
                throw new OperationException("Conflicto de horario para empleado: " + asignacion.getEmpleadoId());
            }
        }
    }

    @Transactional
    public void registrarWalkIn(WalkInRequestDto request) throws Exception {
        if (request.empleadoId() == null || request.empleadoId().isBlank()) {
            throw new OperationException("El campo empleado_id es requerido");
        }
        if (request.clienteId() == null || request.clienteId().isBlank()) {
            throw new OperationException("El campo cliente_id es requerido");
        }
        if (request.servicioIds() == null || request.servicioIds().isEmpty()) {
            throw new OperationException("Debe seleccionar al menos un servicio para el walk-in");
        }

        // 1. Buscar Empleado
        Empleado empleado = empleadoRepository.findById(request.empleadoId())
                .orElseThrow(() -> new OperationException("Empleado no encontrado con ID: " + request.empleadoId()));

        if (!empleado.isActivo()) {
            throw new OperationException("El empleado seleccionado no está activo");
        }

        // 2. Determinar Sucursal activa del empleado
        List<EmpleadoSucursal> asignaciones = empleadoSucursalRepository.findByEmpleadoIdAndActivoTrue(empleado.getId());
        if (asignaciones.isEmpty()) {
            throw new OperationException("El empleado no tiene asignada ninguna sucursal activa");
        }
        Sucursal sucursal = asignaciones.get(0).getSucursal();

        // 3. Buscar Cliente
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new OperationException("Cliente no encontrado con ID: " + request.clienteId()));

        // 4. Calcular duración y preparar servicios
        int duracionTotal = 0;
        List<Servicio> servicios = new ArrayList<>();
        for (String servicioId : request.servicioIds()) {
            Servicio servicio = servicioRepository.findById(servicioId)
                    .orElseThrow(() -> new OperationException("Servicio no encontrado con ID: " + servicioId));
            servicios.add(servicio);
            duracionTotal += servicio.getDuracionMinutos();
        }

        OffsetDateTime inicio = OffsetDateTime.now();
        OffsetDateTime fin = inicio.plusMinutes(duracionTotal);

        // 5. Crear e insertar el AgendaEvento
        AgendaEvento agendaEvento = new AgendaEvento();
        agendaEvento.setSucursal(sucursal);
        agendaEvento.setCliente(cliente);
        agendaEvento.setTipoEvento(TipoEvento.CITA);
        agendaEvento.setEstado(EstadoEvento.FINALIZADO); // Walk-in es atención al instante y finalizada
        agendaEvento.setInicio(inicio);
        agendaEvento.setFin(fin);
        agendaEvento.setNotas("Atención rápida (Walk-in)");
        
        agendaEvento = agendaEventoRepository.save(agendaEvento);

        // 6. Crear los detalles de los servicios en la cita
        for (Servicio servicio : servicios) {
            AgendaEventoDetalle detalle = new AgendaEventoDetalle();
            detalle.setAgendaEvento(agendaEvento);
            detalle.setServicio(servicio);
            detalle.setDuracionEstimadaMinutos(servicio.getDuracionMinutos());
            detalle.setPrecioAcordado(servicio.getPrecioBase());
            detalle.setNotas("Servicio Walk-in");
            agendaEventoDetalleRepository.save(detalle);
        }

        // 7. Asignar el empleado responsable de la cita
        AgendaEventoEmpleado asignacionEmpleado = new AgendaEventoEmpleado();
        asignacionEmpleado.setAgendaEvento(agendaEvento);
        asignacionEmpleado.setEmpleado(empleado);
        asignacionEmpleado.setRolEnEvento(RolEmpleadoEvento.RESPONSABLE);
        agendaEventoEmpleadoRepository.save(asignacionEmpleado);

        // 8. Crear la boleta de venta (Venta)
        List<VentaDetalleRequestDto> detallesVenta = new ArrayList<>();
        BigDecimal totalVenta = BigDecimal.ZERO;

        // Agregar detalles de los servicios prestados a la venta
        for (Servicio servicio : servicios) {
            VentaDetalleRequestDto detDto = new VentaDetalleRequestDto();
            detDto.setTipoItem(TipoItemVenta.SERVICIO);
            detDto.setServicioId(servicio.getId());
            detDto.setEmpleadoId(empleado.getId());
            detDto.setCantidad(1);
            detDto.setPrecioUnitario(servicio.getPrecioBase() != null ? servicio.getPrecioBase() : BigDecimal.ZERO);
            detDto.setDescuento(BigDecimal.ZERO);
            detDto.setNotas("Servicio Walk-in");
            detallesVenta.add(detDto);
            totalVenta = totalVenta.add(detDto.getPrecioUnitario());
        }

        // Agregar detalles de los productos vendidos con cantidad (si los hay)
        if (request.productos() != null) {
            for (WalkInRequestDto.ProductoCantidadDto productoDto : request.productos()) {
                if (productoDto == null || productoDto.productoId() == null || productoDto.productoId().isBlank()) {
                    continue;
                }
                int cantidad = productoDto.cantidad() != null ? productoDto.cantidad() : 0;
                if (cantidad <= 0) {
                    continue;
                }

                Producto producto = productoRepository.findById(productoDto.productoId())
                        .orElseThrow(() -> new OperationException("Producto no encontrado con ID: " + productoDto.productoId()));

                BigDecimal precioUnitario = producto.getPrecioVenta() != null ? producto.getPrecioVenta() : BigDecimal.ZERO;
                VentaDetalleRequestDto detDto = new VentaDetalleRequestDto();
                detDto.setTipoItem(TipoItemVenta.PRODUCTO);
                detDto.setProductoId(producto.getId());
                detDto.setEmpleadoId(empleado.getId());
                detDto.setCantidad(cantidad);
                detDto.setPrecioUnitario(precioUnitario);
                detDto.setDescuento(BigDecimal.ZERO);
                detDto.setNotas("Producto vendido en Walk-in");
                detallesVenta.add(detDto);
                totalVenta = totalVenta.add(precioUnitario.multiply(BigDecimal.valueOf(cantidad)));
            }
        } else if (request.productoIds() != null) {
            for (String productoId : request.productoIds()) {
                if (productoId == null || productoId.isBlank()) continue;
                Producto producto = productoRepository.findById(productoId)
                        .orElseThrow(() -> new OperationException("Producto no encontrado con ID: " + productoId));

                BigDecimal precioUnitario = producto.getPrecioVenta() != null ? producto.getPrecioVenta() : BigDecimal.ZERO;
                VentaDetalleRequestDto detDto = new VentaDetalleRequestDto();
                detDto.setTipoItem(TipoItemVenta.PRODUCTO);
                detDto.setProductoId(producto.getId());
                detDto.setEmpleadoId(empleado.getId());
                detDto.setCantidad(1);
                detDto.setPrecioUnitario(precioUnitario);
                detDto.setDescuento(BigDecimal.ZERO);
                detDto.setNotas("Producto vendido en Walk-in");
                detallesVenta.add(detDto);
                totalVenta = totalVenta.add(precioUnitario);
            }
        }

        // Construir la petición de venta
        VentaRequestDto ventaRequest = new VentaRequestDto();
        ventaRequest.setSucursalId(sucursal.getId());
        ventaRequest.setClienteId(cliente.getId());
        ventaRequest.setSubtotal(totalVenta);
        ventaRequest.setDescuento(BigDecimal.ZERO);
        ventaRequest.setTotal(totalVenta);
        ventaRequest.setNotas("Venta automática generada por Walk-in");
        ventaRequest.setDetalles(detallesVenta);

        ventaRequest.setAgendaEventoId(agendaEvento.getId());

        // Crear venta
        VentaResponseDto ventaDto = ventaService.crear(ventaRequest);

        // Crear el Pago como PAGADO (esto gestiona internamente la reducción de stock y marca la venta como COBRADA)
        PagoRequestDto pagoRequest = new PagoRequestDto();
        pagoRequest.setVentaId(ventaDto.getId());
        pagoRequest.setMonto(ventaDto.getTotal());
        pagoRequest.setMetodoPago(MetodoPago.EFECTIVO);
        pagoRequest.setEstadoPago(EstadoPago.PAGADO);
        pagoRequest.setPagadoEn(OffsetDateTime.now());

        pagoService.crear(pagoRequest);

        logService.info("Walk-in registrado con éxito. Cita ID: " + agendaEvento.getId());
    }

    private AgendaEventoResponseDto.DetalleDto mapDetalleToDto(AgendaEventoDetalle d) {
        String tipo = "SERVICIO";
        String sId = null;
        String sNombre = null;
        if (d.getServicio() != null) {
            sId = d.getServicio().getId();
            sNombre = d.getServicio().getNombre();
            tipo = "SERVICIO";
        } else if (d.getComboServicio() != null) {
            sId = d.getComboServicio().getId();
            sNombre = d.getComboServicio().getNombre();
            tipo = "COMBO";
        } else if (d.getProducto() != null) {
            sNombre = d.getProducto().getNombre() + " (Producto)";
            tipo = "PRODUCTO";
        }
        return new AgendaEventoResponseDto.DetalleDto(
                sId,
                sNombre,
                d.getPrecioAcordado() != null ? d.getPrecioAcordado().doubleValue() : 0.0,
                d.getDuracionEstimadaMinutos(),
                d.getProducto() != null ? d.getProducto().getId() : null,
                d.getProducto() != null ? d.getProducto().getNombre() : null,
                d.getCantidad(),
                tipo
        );
    }

    private AgendaEventoResponseDto mapToResponse(
            AgendaEvento agendaEvento,
            List<AgendaEventoDetalle> detalles,
            List<AgendaEventoEmpleado> empleados
    ) {
        AgendaEventoResponseDto dto = new AgendaEventoResponseDto(agendaEvento);
        dto.setDetalles(Optional.ofNullable(detalles).orElseGet(List::of).stream()
                .map(this::mapDetalleToDto)
                .toList());
        dto.setEmpleados(Optional.ofNullable(empleados).orElseGet(List::of).stream()
                .map(e -> new AgendaEventoResponseDto.EmpleadoDto(
                        e.getEmpleado().getId(),
                        e.getEmpleado().getNombre(),
                        e.getRolEnEvento() != null ? e.getRolEnEvento().name() : null
                ))
                .toList());
        return dto;
    }

    private List<AgendaEventoResponseDto> mapEventosToResponse(List<AgendaEvento> eventos) {
        if (eventos == null || eventos.isEmpty()) {
            return List.of();
        }

        List<String> eventoIds = eventos.stream().map(AgendaEvento::getId).toList();
        Map<String, List<AgendaEventoDetalle>> detallesPorEvento = agendaEventoDetalleRepository
                .findByAgendaEventoIdIn(eventoIds)
                .stream()
                .collect(Collectors.groupingBy(d -> d.getAgendaEvento().getId()));
        Map<String, List<AgendaEventoEmpleado>> empleadosPorEvento = agendaEventoEmpleadoRepository
                .findByAgendaEventoIdIn(eventoIds)
                .stream()
                .collect(Collectors.groupingBy(e -> e.getAgendaEvento().getId()));

        return eventos.stream()
                .map(ae -> mapToResponse(ae, detallesPorEvento.get(ae.getId()), empleadosPorEvento.get(ae.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, String>> obtenerIntervalosOcupados(String empleadoId, java.time.LocalDate fecha) {
        java.time.OffsetDateTime inicioDia = fecha.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toOffsetDateTime();
        java.time.OffsetDateTime finDia = fecha.plusDays(1).atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toOffsetDateTime();

        List<AgendaEventoEmpleado> asignaciones = agendaEventoEmpleadoRepository.findByEmpleadoId(empleadoId);
        List<Map<String, String>> ocupados = new ArrayList<>();

        for (AgendaEventoEmpleado aee : asignaciones) {
            AgendaEvento ae = aee.getAgendaEvento();
            if (ae.getEstado() != EstadoEvento.CANCELADO && ae.getEstado() != EstadoEvento.NO_SHOW) {
                if (ae.getInicio().isBefore(finDia) && ae.getFin().isAfter(inicioDia)) {
                    Map<String, String> intervalo = new HashMap<>();
                    intervalo.put("inicio", ae.getInicio().toString());
                    intervalo.put("fin", ae.getFin().toString());
                    ocupados.add(intervalo);
                }
            }
        }
        return ocupados;
    }

    public @Nullable Object listarPorSucursalPaginado(String sucursalId, PageRequest of) {
        
        Page<AgendaEvento> eventos = agendaEventoRepository.findBySucursalId(sucursalId, of);
        List<AgendaEventoResponseDto> content = mapEventosToResponse(eventos.getContent());
        return new PageImpl<>(content, of, eventos.getTotalElements());
    }

}
