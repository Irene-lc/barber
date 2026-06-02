package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.ProductoRequestDto;
import edu.upb.barber.repository.entity.Producto;
import edu.upb.barber.service.ProductoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        try {
            return ResponseEntity.ok(
                    productoService.listar());
        } catch (Exception e) {
            log.error("Error al listar productos", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(
            @PathVariable String id
    ) {
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
    public ResponseEntity<Void> guardar(
            @RequestBody Producto producto
    ) {
        try {

            productoService.save(producto);

            return ResponseEntity.ok().build();

        } catch (Exception e) {

            log.error("Error al guardar Producto", e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(
            @PathVariable("id") String productoId,
            @RequestBody ProductoRequestDto dto
    ) {
        try {
            productoService.update(productoId, dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al actualizar Producto", e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable String id
    ) {
        try {

            productoService.delete(id);

            return ResponseEntity.ok().build();

        } catch (Exception e) {

            log.error("Error al eliminar Producto", e);

            return ResponseEntity.internalServerError().build();
        }
    }
}
