package edu.upb.barber.service;

import edu.upb.barber.repository.InventarioSucursalRepository;
import edu.upb.barber.repository.ProductoRepository;
import edu.upb.barber.repository.SucursalRepository;
import edu.upb.barber.repository.dto.request.InventarioSucursalRequestDto;
import edu.upb.barber.repository.dto.response.InventarioSucursalResponseDto;
import edu.upb.barber.repository.entity.InventarioSucursal;
import edu.upb.barber.repository.entity.Producto;
import edu.upb.barber.repository.entity.Sucursal;
import edu.upb.barber.service.exception.OperationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class InventarioSucursalService {

    private final InventarioSucursalRepository inventarioSucursalRepository;
    private final ProductoRepository productoRepository;
    private final SucursalRepository sucursalRepository;
    private final LogService logService;

    @Transactional(readOnly = true)
    public List<InventarioSucursalResponseDto> listar() {
        return inventarioSucursalRepository.findAll().stream()
                .map(InventarioSucursalResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InventarioSucursalResponseDto> listarPorSucursal(String sucursalId) {
        return inventarioSucursalRepository.findBySucursalId(sucursalId).stream()
                .map(InventarioSucursalResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<InventarioSucursalResponseDto> findById(String id) {
        return inventarioSucursalRepository.findById(id)
                .map(InventarioSucursalResponseDto::new);
    }

    @Transactional
    public InventarioSucursalResponseDto save(InventarioSucursalRequestDto dto) throws Exception {
        validarDto(dto);

        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new OperationException("Producto no encontrado con id: " + dto.getProductoId()));

        Sucursal sucursal = sucursalRepository.findById(dto.getSucursalId())
                .orElseThrow(() -> new OperationException("Sucursal no encontrada con id: " + dto.getSucursalId()));

        InventarioSucursal inventario = new InventarioSucursal();
        inventario.setProducto(producto);
        inventario.setSucursal(sucursal);
        inventario.setStockActual(dto.getStockActual());
        inventario.setStockMinimo(dto.getStockMinimo() != null ? dto.getStockMinimo() : 0);

        if (dto.getActivo() != null) {
            inventario.setActivo(dto.getActivo());
        }

        logService.info("InventarioSucursal guardado exitosamente para producto: " + dto.getProductoId());
        return new InventarioSucursalResponseDto(inventarioSucursalRepository.save(inventario));
    }

    @Transactional
    public InventarioSucursalResponseDto update(String inventarioId, InventarioSucursalRequestDto dto) throws Exception {
        InventarioSucursal inventario = inventarioSucursalRepository.findById(inventarioId)
                .orElseThrow(() -> new OperationException("InventarioSucursal no encontrado con id: " + inventarioId));

        validarDto(dto);

        inventario.setStockActual(dto.getStockActual());
        inventario.setStockMinimo(dto.getStockMinimo() != null ? dto.getStockMinimo() : inventario.getStockMinimo());

        if (dto.getActivo() != null) {
            inventario.setActivo(dto.getActivo());
        }

        if (dto.getProductoId() != null && !dto.getProductoId().isBlank()) {
            Producto producto = productoRepository.findById(dto.getProductoId())
                    .orElseThrow(() -> new OperationException("Producto no encontrado con id: " + dto.getProductoId()));
            inventario.setProducto(producto);
        }

        if (dto.getSucursalId() != null && !dto.getSucursalId().isBlank()) {
            Sucursal sucursal = sucursalRepository.findById(dto.getSucursalId())
                    .orElseThrow(() -> new OperationException("Sucursal no encontrada con id: " + dto.getSucursalId()));
            inventario.setSucursal(sucursal);
        }

        logService.info("InventarioSucursal actualizado exitosamente: " + inventarioId);
        return new InventarioSucursalResponseDto(inventarioSucursalRepository.save(inventario));
    }

    @Transactional
    public void delete(String id) throws Exception {
        if (!inventarioSucursalRepository.existsById(id)) {
            log.error("Error al eliminar InventarioSucursal. No encontrado con id: {}", id);
            logService.error("Error al eliminar InventarioSucursal. No encontrado con id: " + id);
            throw new OperationException("InventarioSucursal no encontrado con id: " + id);
        }
        inventarioSucursalRepository.deleteById(id);
        logService.info("InventarioSucursal eliminado exitosamente: " + id);
    }

    private void validarDto(InventarioSucursalRequestDto dto) throws Exception {
        if (dto.getStockActual() == null || dto.getStockActual() < 0) {
            log.error("Error en InventarioSucursal. El stock_actual debe ser mayor o igual a cero");
            logService.error("Error en InventarioSucursal. El stock_actual debe ser mayor o igual a cero");
            throw new OperationException("El stock_actual debe ser mayor o igual a cero");
        }
    }
}
