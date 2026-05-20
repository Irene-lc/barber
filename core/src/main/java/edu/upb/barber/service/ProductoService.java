package edu.upb.barber.service;

import edu.upb.barber.dto.request.ProductoRequest;
import edu.upb.barber.dto.response.ProductoResponse;
import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.ProductoRepository;
import edu.upb.barber.repository.entity.Producto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ProductoService {
    private final ProductoRepository repository;
    private final EmpresaRepository empresaRepository;

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {
        return repository.findAll().stream().map(ProductoResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse buscarPorId(String id) throws Exception {
        return repository.findById(id)
                .map(ProductoResponse::fromEntity)
                .orElseThrow(() -> new Exception("Producto no encontrado con id: " + id));
    }

    @Transactional
    public void guardar(ProductoRequest dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }
        Producto entity = new Producto();
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void actualizar(String id, ProductoRequest dto) throws Exception {
        Producto entity = repository.findById(id)
                .orElseThrow(() -> new Exception("Producto no encontrado con id: " + id));
        mapearDesdeDto(dto, entity);
        repository.save(entity);
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        Producto entity = repository.findById(id)
                .orElseThrow(() -> new Exception("Producto no encontrado con id: " + id));
        entity.setActivo(false);
        repository.save(entity);
    }

    private void mapearDesdeDto(ProductoRequest dto, Producto entity) throws Exception {
        entity.setEmpresa(empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new Exception("Empresa no encontrada con id: " + dto.getEmpresaId())));
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setPrecioVenta(dto.getPrecioVenta());
        entity.setActivo(dto.isActivo());
    }
}
