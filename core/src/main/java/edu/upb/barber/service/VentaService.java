package edu.upb.barber.service;

import edu.upb.barber.repository.*;
import edu.upb.barber.repository.dto.request.VentaDetalleRequestDto;
import edu.upb.barber.repository.dto.request.VentaRequestDto;
import edu.upb.barber.repository.dto.response.VentaDetalleResponseDto;
import edu.upb.barber.repository.dto.response.VentaResponseDto;
import edu.upb.barber.repository.entity.*;
import edu.upb.barber.repository.entity.enums.EstadoVenta;
import edu.upb.barber.repository.entity.enums.TipoItemVenta;
import edu.upb.barber.service.exception.OperationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final VentaDetalleRepository ventaDetalleRepository;
    private final SucursalRepository sucursalRepository;
    private final ClienteRepository clienteRepository;
    private final ServicioRepository servicioRepository;
    private final ProductoRepository productoRepository;
    private final ComboServicioRepository comboServicioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final InventarioSucursalRepository inventarioSucursalRepository;
    private final AgendaEventoRepository agendaEventoRepository;
    private final LogService logService;

    @Transactional
    public VentaResponseDto crear(VentaRequestDto request) throws Exception {

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new OperationException("Sucursal no encontrada con ID: " + request.getSucursalId()));

        Cliente cliente = null;
        if (request.getClienteId() != null && !request.getClienteId().isBlank()) {
            cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new OperationException("Cliente no encontrado con ID: " + request.getClienteId()));
        }

        AgendaEvento agendaEvento = null;
        if (request.getAgendaEventoId() != null && !request.getAgendaEventoId().isBlank()) {
            agendaEvento = agendaEventoRepository.findById(request.getAgendaEventoId())
                    .orElseThrow(() -> new OperationException("AgendaEvento no encontrado con ID: " + request.getAgendaEventoId()));
        }

        Venta venta = new Venta();
        venta.setSucursal(sucursal);
        venta.setCliente(cliente);
        venta.setAgendaEvento(agendaEvento);
        venta.setSubtotal(request.getSubtotal() != null ? request.getSubtotal() : BigDecimal.ZERO);
        venta.setDescuento(request.getDescuento() != null ? request.getDescuento() : BigDecimal.ZERO);
        venta.setTotal(request.getTotal() != null ? request.getTotal() : BigDecimal.ZERO);
        venta.setNotas(request.getNotas());
        venta.setEstado(EstadoVenta.ABIERTA);

        venta = ventaRepository.save(venta);

        BigDecimal subtotalCalculado = BigDecimal.ZERO;

        if (request.getDetalles() != null) {
            Map<String, Empleado> empleadosPorId = cargarEmpleados(request.getDetalles());
            Map<String, Servicio> serviciosPorId = cargarServicios(request.getDetalles());
            Map<String, Producto> productosPorId = cargarProductos(request.getDetalles());
            Map<String, ComboServicio> combosPorId = cargarCombos(request.getDetalles());
            Map<String, InventarioSucursal> inventarioPorProductoId = venta.getEstado() == EstadoVenta.COBRADA
                    ? cargarInventarioPorProducto(productosPorId.keySet(), sucursal.getId())
                    : Map.of();
            List<VentaDetalle> detallesNuevos = new ArrayList<>();
            Set<InventarioSucursal> inventariosModificados = new LinkedHashSet<>();

            for (VentaDetalleRequestDto detDto : request.getDetalles()) {
                validarDetalle(detDto);

                VentaDetalle detalle = new VentaDetalle();
                detalle.setVenta(venta);
                detalle.setTipoItem(detDto.getTipoItem());
                detalle.setCantidad(detDto.getCantidad());
                detalle.setPrecioUnitario(detDto.getPrecioUnitario());
                detalle.setDescuento(detDto.getDescuento() != null ? detDto.getDescuento() : BigDecimal.ZERO);

                BigDecimal itemSubtotal = detDto.getPrecioUnitario()
                        .multiply(BigDecimal.valueOf(detDto.getCantidad()))
                        .subtract(detalle.getDescuento());
                detalle.setSubtotal(itemSubtotal);
                subtotalCalculado = subtotalCalculado.add(itemSubtotal);

                detalle.setNotas(detDto.getNotas());

                if (detDto.getEmpleadoId() != null && !detDto.getEmpleadoId().isBlank()) {
                    Empleado empleado = obtenerRequerido(empleadosPorId, detDto.getEmpleadoId(), "Empleado");
                    detalle.setEmpleado(empleado);
                }

                if (detDto.getTipoItem() == TipoItemVenta.SERVICIO) {
                    Servicio servicio = obtenerRequerido(serviciosPorId, detDto.getServicioId(), "Servicio");
                    detalle.setServicio(servicio);
                } else if (detDto.getTipoItem() == TipoItemVenta.PRODUCTO) {
                    Producto producto = obtenerRequerido(productosPorId, detDto.getProductoId(), "Producto");
                    detalle.setProducto(producto);

                    if (venta.getEstado() == EstadoVenta.COBRADA) {
                        InventarioSucursal inv = inventarioPorProductoId.get(producto.getId());
                        if (inv != null) {
                            inv.setStockActual(Math.max(0, inv.getStockActual() - detDto.getCantidad()));
                            inventariosModificados.add(inv);
                        }
                    }
                } else if (detDto.getTipoItem() == TipoItemVenta.COMBO) {
                    ComboServicio combo = obtenerRequerido(combosPorId, detDto.getComboServicioId(), "Combo");
                    detalle.setComboServicio(combo);
                }

                detallesNuevos.add(detalle);
            }
            ventaDetalleRepository.saveAll(detallesNuevos);
            inventarioSucursalRepository.saveAll(inventariosModificados);
        }

        if (request.getSubtotal() == null || request.getSubtotal().compareTo(BigDecimal.ZERO) == 0) {
            venta.setSubtotal(subtotalCalculado);
            BigDecimal descuentoTotal = request.getDescuento() != null ? request.getDescuento() : BigDecimal.ZERO;
            venta.setDescuento(descuentoTotal);
            venta.setTotal(subtotalCalculado.subtract(descuentoTotal));
            venta = ventaRepository.save(venta);
        }

        logService.info("Venta creada exitosamente: " + venta.getId());
        return mapToResponse(venta);
    }

    @Transactional(readOnly = true)
    public List<VentaResponseDto> listar() {
        edu.upb.barber.repository.entity.Usuario currentUser = null;
        if (org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() != null &&
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof edu.upb.barber.repository.entity.Usuario) {
            currentUser = (edu.upb.barber.repository.entity.Usuario) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }

        List<Venta> ventas;
        if (currentUser != null && currentUser.getRol() == edu.upb.barber.repository.entity.enums.RolUsuario.ROLE_CLIENTE) {
            List<String> clienteIds = clienteRepository.findByUsuarioId(currentUser.getId()).stream()
                    .map(Cliente::getId)
                    .toList();
            if (clienteIds.isEmpty()) {
                ventas = List.of();
            } else {
                ventas = ventaRepository.findByClienteIdIn(clienteIds);
            }
        } else if (currentUser != null && currentUser.getEmpresa() != null) {
            ventas = ventaRepository.findBySucursal_Empresa(currentUser.getEmpresa());
        } else {
            ventas = ventaRepository.findAll();
        }

        if (ventas.isEmpty()) {
            return List.of();
        }

        List<String> ventaIds = ventas.stream().map(Venta::getId).toList();
        Map<String, List<VentaDetalle>> detallesPorVenta = ventaDetalleRepository
                .findByVentaIdIn(ventaIds)
                .stream()
                .collect(Collectors.groupingBy(detalle -> detalle.getVenta().getId()));

        return ventas.stream()
                .map(venta -> mapToResponse(venta, detallesPorVenta.get(venta.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<VentaResponseDto> findById(String id) {
        return ventaRepository.findById(id)
                .map(this::mapToResponse);
    }

    private VentaResponseDto mapToResponse(Venta venta) {
        VentaResponseDto res = new VentaResponseDto(venta);
        List<VentaDetalle> detalles = ventaDetalleRepository.findByVentaId(venta.getId());
        return mapToResponse(venta, detalles);
    }

    private VentaResponseDto mapToResponse(Venta venta, List<VentaDetalle> detalles) {
        VentaResponseDto res = new VentaResponseDto(venta);
        res.setDetalles(Optional.ofNullable(detalles).orElseGet(List::of).stream()
                .map(VentaDetalleResponseDto::new)
                .collect(Collectors.toList()));
        return res;
    }

    @Transactional
    public VentaResponseDto update(String id, VentaRequestDto request) throws Exception {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new OperationException("Venta no encontrada con ID: " + id));

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new OperationException("Sucursal no encontrada con ID: " + request.getSucursalId()));

        Cliente cliente = null;
        if (request.getClienteId() != null && !request.getClienteId().isBlank()) {
            cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new OperationException("Cliente no encontrado con ID: " + request.getClienteId()));
        }

        AgendaEvento agendaEvento = null;
        if (request.getAgendaEventoId() != null && !request.getAgendaEventoId().isBlank()) {
            agendaEvento = agendaEventoRepository.findById(request.getAgendaEventoId())
                    .orElseThrow(() -> new OperationException("AgendaEvento no encontrado con ID: " + request.getAgendaEventoId()));
        } else {
            agendaEvento = venta.getAgendaEvento();
        }

        List<VentaDetalle> detallesAnteriores = ventaDetalleRepository.findByVentaId(id);
        if (venta.getEstado() == EstadoVenta.COBRADA) {
            Map<String, InventarioSucursal> inventarioPorProductoId = cargarInventarioPorProducto(detallesAnteriores, venta.getSucursal().getId());
            Set<InventarioSucursal> inventariosModificados = new LinkedHashSet<>();
            for (VentaDetalle det : detallesAnteriores) {
                if (det.getTipoItem() == TipoItemVenta.PRODUCTO && det.getProducto() != null) {
                    InventarioSucursal inv = inventarioPorProductoId.get(det.getProducto().getId());
                    if (inv != null) {
                        inv.setStockActual(inv.getStockActual() + det.getCantidad());
                        inventariosModificados.add(inv);
                    }
                }
            }
            inventarioSucursalRepository.saveAll(inventariosModificados);
        }
        ventaDetalleRepository.deleteAll(detallesAnteriores);

        venta.setSucursal(sucursal);
        venta.setCliente(cliente);
        venta.setAgendaEvento(agendaEvento);
        venta.setSubtotal(request.getSubtotal() != null ? request.getSubtotal() : BigDecimal.ZERO);
        venta.setDescuento(request.getDescuento() != null ? request.getDescuento() : BigDecimal.ZERO);
        venta.setTotal(request.getTotal() != null ? request.getTotal() : BigDecimal.ZERO);
        venta.setNotas(request.getNotas());

        venta = ventaRepository.save(venta);

        BigDecimal subtotalCalculado = BigDecimal.ZERO;

        if (request.getDetalles() != null) {
            Map<String, Empleado> empleadosPorId = cargarEmpleados(request.getDetalles());
            Map<String, Servicio> serviciosPorId = cargarServicios(request.getDetalles());
            Map<String, Producto> productosPorId = cargarProductos(request.getDetalles());
            Map<String, ComboServicio> combosPorId = cargarCombos(request.getDetalles());
            Map<String, InventarioSucursal> inventarioPorProductoId = venta.getEstado() == EstadoVenta.COBRADA
                    ? cargarInventarioPorProducto(productosPorId.keySet(), sucursal.getId())
                    : Map.of();
            List<VentaDetalle> detallesNuevos = new ArrayList<>();
            Set<InventarioSucursal> inventariosModificados = new LinkedHashSet<>();

            for (VentaDetalleRequestDto detDto : request.getDetalles()) {
                validarDetalle(detDto);

                VentaDetalle detalle = new VentaDetalle();
                detalle.setVenta(venta);
                detalle.setTipoItem(detDto.getTipoItem());
                detalle.setCantidad(detDto.getCantidad());
                detalle.setPrecioUnitario(detDto.getPrecioUnitario());
                detalle.setDescuento(detDto.getDescuento() != null ? detDto.getDescuento() : BigDecimal.ZERO);

                BigDecimal itemSubtotal = detDto.getPrecioUnitario()
                        .multiply(BigDecimal.valueOf(detDto.getCantidad()))
                        .subtract(detalle.getDescuento());
                detalle.setSubtotal(itemSubtotal);
                subtotalCalculado = subtotalCalculado.add(itemSubtotal);

                detalle.setNotas(detDto.getNotas());

                if (detDto.getEmpleadoId() != null && !detDto.getEmpleadoId().isBlank()) {
                    Empleado empleado = obtenerRequerido(empleadosPorId, detDto.getEmpleadoId(), "Empleado");
                    detalle.setEmpleado(empleado);
                }

                if (detDto.getTipoItem() == TipoItemVenta.SERVICIO) {
                    Servicio servicio = obtenerRequerido(serviciosPorId, detDto.getServicioId(), "Servicio");
                    detalle.setServicio(servicio);
                } else if (detDto.getTipoItem() == TipoItemVenta.PRODUCTO) {
                    Producto producto = obtenerRequerido(productosPorId, detDto.getProductoId(), "Producto");
                    detalle.setProducto(producto);

                    if (venta.getEstado() == EstadoVenta.COBRADA) {
                        InventarioSucursal inv = inventarioPorProductoId.get(producto.getId());
                        if (inv != null) {
                            inv.setStockActual(Math.max(0, inv.getStockActual() - detDto.getCantidad()));
                            inventariosModificados.add(inv);
                        }
                    }
                } else if (detDto.getTipoItem() == TipoItemVenta.COMBO) {
                    ComboServicio combo = obtenerRequerido(combosPorId, detDto.getComboServicioId(), "Combo");
                    detalle.setComboServicio(combo);
                }

                detallesNuevos.add(detalle);
            }
            ventaDetalleRepository.saveAll(detallesNuevos);
            inventarioSucursalRepository.saveAll(inventariosModificados);
        }

        if (request.getSubtotal() == null || request.getSubtotal().compareTo(BigDecimal.ZERO) == 0) {
            venta.setSubtotal(subtotalCalculado);
            BigDecimal descuentoTotal = request.getDescuento() != null ? request.getDescuento() : BigDecimal.ZERO;
            venta.setDescuento(descuentoTotal);
            venta.setTotal(subtotalCalculado.subtract(descuentoTotal));
            venta = ventaRepository.save(venta);
        }

        logService.info("Venta actualizada exitosamente: " + id);
        return mapToResponse(venta);
    }

    @Transactional
    public void delete(String id) throws Exception {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new OperationException("Venta no encontrada con ID: " + id));

        List<VentaDetalle> detalles = ventaDetalleRepository.findByVentaId(id);
        if (venta.getEstado() == EstadoVenta.COBRADA) {
            Map<String, InventarioSucursal> inventarioPorProductoId = cargarInventarioPorProducto(detalles, venta.getSucursal().getId());
            Set<InventarioSucursal> inventariosModificados = new LinkedHashSet<>();
            for (VentaDetalle det : detalles) {
                if (det.getTipoItem() == TipoItemVenta.PRODUCTO && det.getProducto() != null) {
                    InventarioSucursal inv = inventarioPorProductoId.get(det.getProducto().getId());
                    if (inv != null) {
                        inv.setStockActual(inv.getStockActual() + det.getCantidad());
                        inventariosModificados.add(inv);
                    }
                }
            }
            inventarioSucursalRepository.saveAll(inventariosModificados);
        }

        ventaDetalleRepository.deleteAll(detalles);
        ventaRepository.delete(venta);
        logService.info("Venta eliminada exitosamente: " + id);
    }

    @Transactional
    public void descontarStockVenta(Venta venta) {
        if (venta == null) return;
        List<VentaDetalle> detalles = ventaDetalleRepository.findByVentaId(venta.getId());
        Map<String, InventarioSucursal> inventarioPorProductoId = cargarInventarioPorProducto(detalles, venta.getSucursal().getId());
        Set<InventarioSucursal> inventariosModificados = new LinkedHashSet<>();
        for (VentaDetalle det : detalles) {
            if (det.getTipoItem() == TipoItemVenta.PRODUCTO && det.getProducto() != null) {
                InventarioSucursal inv = inventarioPorProductoId.get(det.getProducto().getId());
                if (inv != null) {
                    inv.setStockActual(Math.max(0, inv.getStockActual() - det.getCantidad()));
                    inventariosModificados.add(inv);
                    log.info("Stock descontado para producto: {}, sucursal: {}, cantidad: {}",
                            det.getProducto().getId(), venta.getSucursal().getId(), det.getCantidad());
                }
            }
        }
        inventarioSucursalRepository.saveAll(inventariosModificados);
    }

    @Transactional
    public void restaurarStockVenta(Venta venta) {
        if (venta == null) return;
        List<VentaDetalle> detalles = ventaDetalleRepository.findByVentaId(venta.getId());
        Map<String, InventarioSucursal> inventarioPorProductoId = cargarInventarioPorProducto(detalles, venta.getSucursal().getId());
        Set<InventarioSucursal> inventariosModificados = new LinkedHashSet<>();
        for (VentaDetalle det : detalles) {
            if (det.getTipoItem() == TipoItemVenta.PRODUCTO && det.getProducto() != null) {
                InventarioSucursal inv = inventarioPorProductoId.get(det.getProducto().getId());
                if (inv != null) {
                    inv.setStockActual(inv.getStockActual() + det.getCantidad());
                    inventariosModificados.add(inv);
                    log.info("Stock restaurado para producto: {}, sucursal: {}, cantidad: {}",
                            det.getProducto().getId(), venta.getSucursal().getId(), det.getCantidad());
                }
            }
        }
        inventarioSucursalRepository.saveAll(inventariosModificados);
    }

    private Map<String, Empleado> cargarEmpleados(List<VentaDetalleRequestDto> detalles) {
        List<String> ids = detalles.stream()
                .filter(Objects::nonNull)
                .map(VentaDetalleRequestDto::getEmpleadoId)
                .filter(id -> id != null && !id.isBlank())
                .distinct()
                .toList();
        if (ids.isEmpty()) return Map.of();
        return empleadoRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Empleado::getId, empleado -> empleado));
    }

    private Map<String, Servicio> cargarServicios(List<VentaDetalleRequestDto> detalles) {
        List<String> ids = detalles.stream()
                .filter(Objects::nonNull)
                .filter(det -> det.getTipoItem() == TipoItemVenta.SERVICIO)
                .map(VentaDetalleRequestDto::getServicioId)
                .filter(id -> id != null && !id.isBlank())
                .distinct()
                .toList();
        if (ids.isEmpty()) return Map.of();
        return servicioRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Servicio::getId, servicio -> servicio));
    }

    private Map<String, Producto> cargarProductos(List<VentaDetalleRequestDto> detalles) {
        List<String> ids = detalles.stream()
                .filter(Objects::nonNull)
                .filter(det -> det.getTipoItem() == TipoItemVenta.PRODUCTO)
                .map(VentaDetalleRequestDto::getProductoId)
                .filter(id -> id != null && !id.isBlank())
                .distinct()
                .toList();
        if (ids.isEmpty()) return Map.of();
        return productoRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Producto::getId, producto -> producto));
    }

    private Map<String, ComboServicio> cargarCombos(List<VentaDetalleRequestDto> detalles) {
        List<String> ids = detalles.stream()
                .filter(Objects::nonNull)
                .filter(det -> det.getTipoItem() == TipoItemVenta.COMBO)
                .map(VentaDetalleRequestDto::getComboServicioId)
                .filter(id -> id != null && !id.isBlank())
                .distinct()
                .toList();
        if (ids.isEmpty()) return Map.of();
        return comboServicioRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(ComboServicio::getId, combo -> combo));
    }

    private Map<String, InventarioSucursal> cargarInventarioPorProducto(Collection<String> productoIds, String sucursalId) {
        List<String> ids = productoIds.stream()
                .filter(id -> id != null && !id.isBlank())
                .distinct()
                .toList();
        if (ids.isEmpty()) return Map.of();
        return inventarioSucursalRepository.findByProductoIdInAndSucursalId(ids, sucursalId).stream()
                .collect(Collectors.toMap(inv -> inv.getProducto().getId(), inv -> inv));
    }

    private Map<String, InventarioSucursal> cargarInventarioPorProducto(List<VentaDetalle> detalles, String sucursalId) {
        List<String> productoIds = detalles.stream()
                .filter(det -> det.getTipoItem() == TipoItemVenta.PRODUCTO && det.getProducto() != null)
                .map(det -> det.getProducto().getId())
                .distinct()
                .toList();
        return cargarInventarioPorProducto(productoIds, sucursalId);
    }

    private <T> T obtenerRequerido(Map<String, T> entidadesPorId, String id, String nombreEntidad) throws OperationException {
        T entidad = entidadesPorId.get(id);
        if (entidad == null) {
            throw new OperationException(nombreEntidad + " no encontrado con ID: " + id);
        }
        return entidad;
    }

    private void validarDetalle(VentaDetalleRequestDto detDto) throws OperationException {
        if (detDto == null) {
            throw new OperationException("El detalle de venta no puede ser null");
        }
        if (detDto.getCantidad() <= 0) {
            throw new OperationException("La cantidad debe ser mayor que cero");
        }
        if (detDto.getPrecioUnitario() == null || detDto.getPrecioUnitario().compareTo(BigDecimal.ZERO) < 0) {
            throw new OperationException("El precio unitario debe ser mayor o igual a cero");
        }
    }
}
