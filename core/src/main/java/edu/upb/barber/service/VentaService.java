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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

        Venta venta = new Venta();
        venta.setSucursal(sucursal);
        venta.setCliente(cliente);
        venta.setSubtotal(request.getSubtotal() != null ? request.getSubtotal() : BigDecimal.ZERO);
        venta.setDescuento(request.getDescuento() != null ? request.getDescuento() : BigDecimal.ZERO);
        venta.setTotal(request.getTotal() != null ? request.getTotal() : BigDecimal.ZERO);
        venta.setNotas(request.getNotas());
        venta.setEstado(EstadoVenta.ABIERTA);

        venta = ventaRepository.save(venta);

        BigDecimal subtotalCalculado = BigDecimal.ZERO;

        if (request.getDetalles() != null) {
            for (VentaDetalleRequestDto detDto : request.getDetalles()) {
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
                    Empleado empleado = empleadoRepository.findById(detDto.getEmpleadoId())
                            .orElseThrow(() -> new OperationException("Empleado no encontrado con ID: " + detDto.getEmpleadoId()));
                    detalle.setEmpleado(empleado);
                }

                if (detDto.getTipoItem() == TipoItemVenta.SERVICIO) {
                    Servicio servicio = servicioRepository.findById(detDto.getServicioId())
                            .orElseThrow(() -> new OperationException("Servicio no encontrado con ID: " + detDto.getServicioId()));
                    detalle.setServicio(servicio);
                } else if (detDto.getTipoItem() == TipoItemVenta.PRODUCTO) {
                    Producto producto = productoRepository.findById(detDto.getProductoId())
                            .orElseThrow(() -> new OperationException("Producto no encontrado con ID: " + detDto.getProductoId()));
                    detalle.setProducto(producto);

                    Optional<InventarioSucursal> invOpt = inventarioSucursalRepository
                            .findByProductoIdAndSucursalId(producto.getId(), sucursal.getId());
                    if (invOpt.isPresent()) {
                        InventarioSucursal inv = invOpt.get();
                        inv.setStockActual(Math.max(0, inv.getStockActual() - detDto.getCantidad()));
                        inventarioSucursalRepository.save(inv);
                    }
                } else if (detDto.getTipoItem() == TipoItemVenta.COMBO) {
                    ComboServicio combo = comboServicioRepository.findById(detDto.getComboServicioId())
                            .orElseThrow(() -> new OperationException("Combo no encontrado con ID: " + detDto.getComboServicioId()));
                    detalle.setComboServicio(combo);
                }

                ventaDetalleRepository.save(detalle);
            }
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
        return ventaRepository.findAll().stream()
                .map(this::mapToResponse)
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
        res.setDetalles(detalles.stream()
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

        List<VentaDetalle> detallesAnteriores = ventaDetalleRepository.findByVentaId(id);
        for (VentaDetalle det : detallesAnteriores) {
            if (det.getTipoItem() == TipoItemVenta.PRODUCTO && det.getProducto() != null) {
                Optional<InventarioSucursal> invOpt = inventarioSucursalRepository
                        .findByProductoIdAndSucursalId(det.getProducto().getId(), venta.getSucursal().getId());
                if (invOpt.isPresent()) {
                    InventarioSucursal inv = invOpt.get();
                    inv.setStockActual(inv.getStockActual() + det.getCantidad());
                    inventarioSucursalRepository.save(inv);
                }
            }
        }
        ventaDetalleRepository.deleteAll(detallesAnteriores);

        venta.setSucursal(sucursal);
        venta.setCliente(cliente);
        venta.setSubtotal(request.getSubtotal() != null ? request.getSubtotal() : BigDecimal.ZERO);
        venta.setDescuento(request.getDescuento() != null ? request.getDescuento() : BigDecimal.ZERO);
        venta.setTotal(request.getTotal() != null ? request.getTotal() : BigDecimal.ZERO);
        venta.setNotas(request.getNotas());

        venta = ventaRepository.save(venta);

        BigDecimal subtotalCalculado = BigDecimal.ZERO;

        if (request.getDetalles() != null) {
            for (VentaDetalleRequestDto detDto : request.getDetalles()) {
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
                    Empleado empleado = empleadoRepository.findById(detDto.getEmpleadoId())
                            .orElseThrow(() -> new OperationException("Empleado no encontrado con ID: " + detDto.getEmpleadoId()));
                    detalle.setEmpleado(empleado);
                }

                if (detDto.getTipoItem() == TipoItemVenta.SERVICIO) {
                    Servicio servicio = servicioRepository.findById(detDto.getServicioId())
                            .orElseThrow(() -> new OperationException("Servicio no encontrado con ID: " + detDto.getServicioId()));
                    detalle.setServicio(servicio);
                } else if (detDto.getTipoItem() == TipoItemVenta.PRODUCTO) {
                    Producto producto = productoRepository.findById(detDto.getProductoId())
                            .orElseThrow(() -> new OperationException("Producto no encontrado con ID: " + detDto.getProductoId()));
                    detalle.setProducto(producto);

                    Optional<InventarioSucursal> invOpt = inventarioSucursalRepository
                            .findByProductoIdAndSucursalId(producto.getId(), sucursal.getId());
                    if (invOpt.isPresent()) {
                        InventarioSucursal inv = invOpt.get();
                        inv.setStockActual(Math.max(0, inv.getStockActual() - detDto.getCantidad()));
                        inventarioSucursalRepository.save(inv);
                    }
                } else if (detDto.getTipoItem() == TipoItemVenta.COMBO) {
                    ComboServicio combo = comboServicioRepository.findById(detDto.getComboServicioId())
                            .orElseThrow(() -> new OperationException("Combo no encontrado con ID: " + detDto.getComboServicioId()));
                    detalle.setComboServicio(combo);
                }

                ventaDetalleRepository.save(detalle);
            }
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
        for (VentaDetalle det : detalles) {
            if (det.getTipoItem() == TipoItemVenta.PRODUCTO && det.getProducto() != null) {
                Optional<InventarioSucursal> invOpt = inventarioSucursalRepository
                        .findByProductoIdAndSucursalId(det.getProducto().getId(), venta.getSucursal().getId());
                if (invOpt.isPresent()) {
                    InventarioSucursal inv = invOpt.get();
                    inv.setStockActual(inv.getStockActual() + det.getCantidad());
                    inventarioSucursalRepository.save(inv);
                }
            }
        }

        ventaDetalleRepository.deleteAll(detalles);
        ventaRepository.delete(venta);
        logService.info("Venta eliminada exitosamente: " + id);
    }
}
