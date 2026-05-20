package edu.upb.barber.service;

import edu.upb.barber.dto.request.VentaDetalleRequest;
import edu.upb.barber.dto.response.VentaDetalleResponse;
import edu.upb.barber.repository.ComboServicioRepository;
import edu.upb.barber.repository.EmpleadoRepository;
import edu.upb.barber.repository.ProductoRepository;
import edu.upb.barber.repository.ServicioRepository;
import edu.upb.barber.repository.VentaDetalleRepository;
import edu.upb.barber.repository.VentaRepository;
import edu.upb.barber.repository.entity.VentaDetalle;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class VentaDetalleService {
    private final VentaDetalleRepository repository;
    private final VentaRepository ventaRepository;
    private final ServicioRepository servicioRepository;
    private final ProductoRepository productoRepository;
    private final ComboServicioRepository comboServicioRepository;
    private final EmpleadoRepository empleadoRepository;

    @Transactional(readOnly = true)
    public List<VentaDetalleResponse> listar() {
        return repository.findAll().stream().map(VentaDetalleResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public VentaDetalleResponse buscarPorId(String id) throws Exception {
        return repository.findById(id)
                .map(VentaDetalleResponse::fromEntity)
                .orElseThrow(() -> new Exception("VentaDetalle no encontrado con id: " + id));
    }

    @Transactional
    public void guardar(VentaDetalleRequest dto) throws Exception {
        if (dto.getTipoItem() == null) {
            throw new Exception("El campo tipoItem es requerido");
        }
        VentaDetalle entity = new VentaDetalle();
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void actualizar(String id, VentaDetalleRequest dto) throws Exception {
        VentaDetalle entity = repository.findById(id)
                .orElseThrow(() -> new Exception("VentaDetalle no encontrado con id: " + id));
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        if (!repository.existsById(id)) {
            throw new Exception("VentaDetalle no encontrado con id: " + id);
        }
        repository.deleteById(id);
    }

    private void mapearDesdeDto(VentaDetalleRequest dto, VentaDetalle entity) throws Exception {
        entity.setVenta(ventaRepository.findById(dto.getVentaId())
                .orElseThrow(() -> new Exception("Venta no encontrada con id: " + dto.getVentaId())));
        entity.setTipoItem(dto.getTipoItem());
        if (dto.getServicioId() != null) {
            entity.setServicio(servicioRepository.findById(dto.getServicioId())
                    .orElseThrow(() -> new Exception("Servicio no encontrado con id: " + dto.getServicioId())));
        }
        if (dto.getProductoId() != null) {
            entity.setProducto(productoRepository.findById(dto.getProductoId())
                    .orElseThrow(() -> new Exception("Producto no encontrado con id: " + dto.getProductoId())));
        }
        if (dto.getComboServicioId() != null) {
            entity.setComboServicio(comboServicioRepository.findById(dto.getComboServicioId())
                    .orElseThrow(() -> new Exception("ComboServicio no encontrado con id: " + dto.getComboServicioId())));
        }
        if (dto.getEmpleadoId() != null) {
            entity.setEmpleado(empleadoRepository.findById(dto.getEmpleadoId())
                    .orElseThrow(() -> new Exception("Empleado no encontrado con id: " + dto.getEmpleadoId())));
        }
        entity.setCantidad(dto.getCantidad());
        entity.setPrecioUnitario(dto.getPrecioUnitario());
        entity.setDescuento(dto.getDescuento());
        entity.setSubtotal(dto.getSubtotal());
        entity.setNotas(dto.getNotas());
    }
}
