package edu.upb.barber.service;

import edu.upb.barber.dto.request.InventarioSucursalRequest;
import edu.upb.barber.dto.response.InventarioSucursalResponse;
import edu.upb.barber.repository.InventarioSucursalRepository;
import edu.upb.barber.repository.ProductoRepository;
import edu.upb.barber.repository.SucursalRepository;
import edu.upb.barber.repository.entity.InventarioSucursal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class InventarioSucursalService {
    private final InventarioSucursalRepository repository;
    private final ProductoRepository productoRepository;
    private final SucursalRepository sucursalRepository;

    @Transactional(readOnly = true)
    public List<InventarioSucursalResponse> listar() {
        return repository.findAll().stream().map(InventarioSucursalResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public InventarioSucursalResponse buscarPorId(String id) throws Exception {
        return repository.findById(id)
                .map(InventarioSucursalResponse::fromEntity)
                .orElseThrow(() -> new Exception("InventarioSucursal no encontrado con id: " + id));
    }

    @Transactional
    public void guardar(InventarioSucursalRequest dto) throws Exception {
        InventarioSucursal entity = new InventarioSucursal();
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void actualizar(String id, InventarioSucursalRequest dto) throws Exception {
        InventarioSucursal entity = repository.findById(id)
                .orElseThrow(() -> new Exception("InventarioSucursal no encontrado con id: " + id));
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        InventarioSucursal entity = repository.findById(id)
                .orElseThrow(() -> new Exception("InventarioSucursal no encontrado con id: " + id));
        entity.setActivo(false);
        repository.save(entity);
    }

    private void mapearDesdeDto(InventarioSucursalRequest dto, InventarioSucursal entity) throws Exception {
        entity.setProducto(productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new Exception("Producto no encontrado con id: " + dto.getProductoId())));
        entity.setSucursal(sucursalRepository.findById(dto.getSucursalId())
                .orElseThrow(() -> new Exception("Sucursal no encontrada con id: " + dto.getSucursalId())));
        entity.setStockActual(dto.getStockActual());
        entity.setStockMinimo(dto.getStockMinimo());
        entity.setActivo(dto.isActivo());
    }
}
