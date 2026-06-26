package edu.upb.barber.service;

import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.ProductoRepository;
import edu.upb.barber.repository.dto.request.ProductoRequestDto;
import edu.upb.barber.repository.dto.response.ProductoDtoTest;
import edu.upb.barber.repository.dto.response.ProductoResponseDto;
import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.Producto;
import edu.upb.barber.service.exception.OperationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final EmpresaRepository empresaRepository;
    private final LogService logService;

    @Transactional(readOnly = true)
    public List<ProductoResponseDto> listar() {
        return productoRepository.findAll().stream()
                .map(ProductoResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ProductoDtoTest> findAllPag(
            String pNombre,
            Pageable page) {
        return productoRepository
                .findAllPag(pNombre, page)
                .map(ProductoDtoTest::new);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDto> listarPorEmpresa(String empresaId) {
        return productoRepository.findByEmpresaId(empresaId).stream()
                .map(ProductoResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProductoResponseDto> findById(String id) {
        return productoRepository.findById(id)
                .map(ProductoResponseDto::new);
    }

    @Transactional
    public ProductoResponseDto save(ProductoRequestDto dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            log.error("Error al guardar producto. El campo nombre es requerido");
            logService.error("Error al guardar producto. El campo nombre es requerido");
            throw new OperationException("El campo nombre es requerido");
        }
        if (dto.getPrecio() == null || dto.getPrecio() < 0) {
            log.error("Error al guardar producto. El precio debe ser mayor o igual a cero");
            logService.error("Error al guardar producto. El precio debe ser mayor o igual a cero");
            throw new OperationException("El precio debe ser mayor o igual a cero");
        }
        if (dto.getEmpresaId() == null || dto.getEmpresaId().isBlank()) {
            log.error("Error al guardar producto. El campo empresa_id es requerido");
            logService.error("Error al guardar producto. El campo empresa_id es requerido");
            throw new OperationException("El campo empresa_id es requerido");
        }

        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new OperationException("Empresa no encontrada con id: " + dto.getEmpresaId()));

        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setImagenUrl(dto.getImagenUrl() != null && !dto.getImagenUrl().isBlank() ? dto.getImagenUrl() : null);
        producto.setPrecioVenta(BigDecimal.valueOf(dto.getPrecio()));
        producto.setEmpresa(empresa);

        if (dto.getActivo() != null) {
            producto.setActivo(dto.getActivo());
        }

        logService.info("Producto guardado exitosamente: " + dto.getNombre());
        return new ProductoResponseDto(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponseDto update(String productoId, ProductoRequestDto dto) throws Exception {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new OperationException("Producto no encontrado con id: " + productoId));

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            log.error("Error al actualizar producto. El campo nombre es requerido");
            logService.error("Error al actualizar producto. El campo nombre es requerido");
            throw new OperationException("El campo nombre es requerido");
        }
        if (dto.getPrecio() == null || dto.getPrecio() < 0) {
            log.error("Error al actualizar producto. El precio debe ser mayor o igual a cero");
            logService.error("Error al actualizar producto. El precio debe ser mayor o igual a cero");
            throw new OperationException("El precio debe ser mayor o igual a cero");
        }

        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        if (dto.getImagenUrl() != null) {
            producto.setImagenUrl(dto.getImagenUrl().isBlank() ? null : dto.getImagenUrl());
        }
        producto.setPrecioVenta(BigDecimal.valueOf(dto.getPrecio()));

        if (dto.getEmpresaId() != null && !dto.getEmpresaId().isBlank()) {
            Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new OperationException("Empresa no encontrada con id: " + dto.getEmpresaId()));
            producto.setEmpresa(empresa);
        }

        if (dto.getActivo() != null) {
            producto.setActivo(dto.getActivo());
        }

        logService.info("Producto actualizado exitosamente: " + productoId);
        return new ProductoResponseDto(productoRepository.save(producto));
    }

    @Transactional
    public void delete(String id) throws Exception {
        if (!productoRepository.existsById(id)) {
            log.error("Error al eliminar producto. No encontrado con id: {}", id);
            logService.error("Error al eliminar producto. No encontrado con id: " + id);
            throw new OperationException("Producto no encontrado con id: " + id);
        }
        productoRepository.deleteById(id);
        logService.info("Producto eliminado exitosamente: " + id);
    }
}
