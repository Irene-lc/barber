package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.AgendaEventoEmpleadoRequest;
import edu.upb.barber.repository.dto.response.AgendaEventoEmpleadoResponse;
import edu.upb.barber.service.AgendaEventoEmpleadoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@Slf4j
@Controller
@RequestMapping("/api/agenda-empleados")
public class AgendaEventoEmpleadoController {
    private final AgendaEventoEmpleadoService service;

    @GetMapping
    public ResponseEntity<List<AgendaEventoEmpleadoResponse>> listar() {
        try {
            return ResponseEntity.ok(service.listar());
        } catch (Exception e) {
            log.error("Error al listar agenda-empleados", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendaEventoEmpleadoResponse> buscarPorId(@PathVariable String id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (Exception e) {
            log.error("Error al buscar agenda-empleado con id {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> guardar(@RequestBody AgendaEventoEmpleadoRequest dto) {
        try {
            service.guardar(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al guardar agenda-empleado", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable String id, @RequestBody AgendaEventoEmpleadoRequest dto) {
        try {
            service.actualizar(id, dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al actualizar agenda-empleado con id {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            service.eliminar(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar agenda-empleado con id {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
