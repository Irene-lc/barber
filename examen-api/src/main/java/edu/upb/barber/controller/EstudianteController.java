package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.EstudianteRequestDto;
import edu.upb.barber.repository.entity.Estudiante;
import edu.upb.barber.repository.entity.Materia;
import edu.upb.barber.service.EstudianteService;
import edu.upb.barber.service.MateriaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping("/api/estudiantes")
public class EstudianteController {
    private final EstudianteService service;

    @GetMapping()
    public ResponseEntity<List<Estudiante>> estudiantes() {
        try {
            return ResponseEntity.ok(service.listar());
        }catch (Exception e) {
            log.error("Error al listar eventos", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{empresaId}")
    public ResponseEntity<Void> guardar(
            @PathVariable("empresaId") String materiaId,
            @RequestBody Estudiante estudiante) {
        try {
            this.service.guardar(materiaId, estudiante);
            return ResponseEntity.ok().build();
        }catch (Exception e) {
            log.error("Error al listar eventos", e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable("id")String materiaId,
                                           @RequestBody EstudianteRequestDto estudiante) {
        try {
            this.service.update(materiaId, estudiante);
            return ResponseEntity.ok().build();
        }catch (Exception e) {
            log.error("Error al actualizar estudiante", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
