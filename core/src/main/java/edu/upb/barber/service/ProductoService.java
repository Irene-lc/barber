package edu.upb.barber.service;

import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.ProductoRepository;
import edu.upb.barber.repository.dto.request.ProductoRequestDto;
import edu.upb.barber.repository.dto.response.ProductoResponseDto;
import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.Producto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final EmpresaRepository empresaRepository;

    @Transactional(readOnly = true)
    public List<ProductoResponseDto> listar() {
        return productoRepository.findAll().stream()
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
            throw new Exception("El campo nombre es requerido");
        }
        if (dto.getPrecio() == null || dto.getPrecio() < 0) {
            throw new Exception("El precio debe ser mayor o igual a cero");
        }
        if (dto.getEmpresaId() == null || dto.getEmpresaId().isBlank()) {
            throw new Exception("El campo empresa_id es requerido");
        }

        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new Exception("Empresa no encontrada con id: " + dto.getEmpresaId()));

        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecioVenta(BigDecimal.valueOf(dto.getPrecio()));
        producto.setEmpresa(empresa);

        if (dto.getActivo() != null) {
            producto.setActivo(dto.getActivo());
        }

        return new ProductoResponseDto(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponseDto update(String productoId, ProductoRequestDto dto) throws Exception {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new Exception("Producto no encontrado con id: " + productoId));

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }
        if (dto.getPrecio() == null || dto.getPrecio() < 0) {
            throw new Exception("El precio debe ser mayor o igual a cero");
        }

        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecioVenta(BigDecimal.valueOf(dto.getPrecio()));

        if (dto.getEmpresaId() != null && !dto.getEmpresaId().isBlank()) {
            Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new Exception("Empresa no encontrada con id: " + dto.getEmpresaId()));
            producto.setEmpresa(empresa);
        }

        if (dto.getActivo() != null) {
            producto.setActivo(dto.getActivo());
        }

        return new ProductoResponseDto(productoRepository.save(producto));
    }

    @Transactional
    public void delete(String id) throws Exception {
        if (!productoRepository.existsById(id)) {
            throw new Exception("Producto no encontrado con id: " + id);
        }
        productoRepository.deleteById(id);
    }
}
