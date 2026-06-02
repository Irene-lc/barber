package edu.upb.barber.service;

import edu.upb.barber.repository.ProductoRepository;
import edu.upb.barber.repository.dto.request.ProductoRequestDto;
import edu.upb.barber.repository.entity.Producto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Producto> findById(String id) {
        return productoRepository.findById(id);
    }

    @Transactional
    public void save(Producto producto) {
        productoRepository.save(producto);
    }

    @Transactional
    public void delete(String id) {
        productoRepository.deleteById(id);
    }
    @Transactional
    public void update(String productoId, ProductoRequestDto dto) throws Exception {

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

        productoRepository.save(producto);
    }
}
