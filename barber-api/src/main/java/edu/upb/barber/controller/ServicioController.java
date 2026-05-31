package edu.upb.barber.controller;

import edu.upb.barber.repository.entity.Servicio;
import edu.upb.barber.service.ServicioService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/servicios")
public class ServicioController {

    private final ServicioService servicioService;

    @GetMapping
    public ResponseEntity<List<Servicio>> listar() {
        try {
            return ResponseEntity.ok(
                    servicioService.listar());
        } catch (Exception e) {
            log.error("Error al listar servicios", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Servicio> obtenerPorId(
            @PathVariable String id
    ) {
        try {

            return servicioService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {

            log.error("Error al obtener Servicio", e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> guardar(
            @RequestBody Servicio servicio
    ) {
        try {

            servicioService.save(servicio);

            return ResponseEntity.ok().build();

        } catch (Exception e) {

            log.error("Error al guardar Servicio", e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable String id
    ) {
        try {

            servicioService.delete(id);

            return ResponseEntity.ok().build();

        } catch (Exception e) {

            log.error("Error al eliminar Servicio", e);

            return ResponseEntity.internalServerError().build();
        }
    }
}
