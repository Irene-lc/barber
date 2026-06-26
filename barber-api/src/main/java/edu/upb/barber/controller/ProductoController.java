package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.ProductoRequestDto;
import edu.upb.barber.repository.dto.response.ProductoResponseDto;
import edu.upb.barber.service.ProductoService;
import edu.upb.barber.service.exception.OperationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<Page<ProductoResponseDto>> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String empresaId,
            @RequestParam(required = false) Boolean activo,
            Pageable pageable
    ) {
        try {
            return ResponseEntity.ok(productoService.listarPaginado(nombre, empresaId, activo, pageable));
        } catch (Exception e) {
            log.error("Error al listar productos.", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<ProductoResponseDto>> listarPorEmpresa(@PathVariable String empresaId) {
        try {
            return ResponseEntity.ok(productoService.listarPorEmpresa(empresaId));
        } catch (Exception e) {
            log.error("Error al listar productos por empresa", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> obtenerPorId(@PathVariable String id) {
        try {
            return productoService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener Producto", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDto> guardar(@RequestBody ProductoRequestDto dto) {
        try {
            return ResponseEntity.ok(productoService.save(dto));
        } catch (OperationException e) {
            log.error("Error al guardar Producto. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al guardar Producto", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> actualizar(
            @PathVariable("id") String productoId,
            @RequestBody ProductoRequestDto dto
    ) {
        try {
            return ResponseEntity.ok(productoService.update(productoId, dto));
        } catch (OperationException e) {
            log.error("Error al actualizar Producto. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al actualizar Producto", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            productoService.delete(id);
            return ResponseEntity.ok().build();
        } catch (OperationException e) {
            log.error("Error al eliminar Producto. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al eliminar Producto", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

