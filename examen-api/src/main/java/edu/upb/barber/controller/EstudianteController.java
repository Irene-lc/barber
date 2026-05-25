package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.EstudianteRequestDto;
import edu.upb.barber.repository.dto.response.EstudianteResponseDto;
import edu.upb.barber.service.EstudianteService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/estudiante")
public class EstudianteController {
    private final EstudianteService estudianteService;

    @GetMapping
    public ResponseEntity<List<EstudianteResponseDto>> estudiantes() {
        try {
            return ResponseEntity.ok(estudianteService.listar());
        } catch (Exception e) {
            log.error("Error al listar estudiantes", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> guardar(
            @RequestBody EstudianteRequestDto estudiante
    ) {
        try {
            estudianteService.save(estudiante);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al guardar estudiante", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable("id") String estudianteId,
                                           @RequestBody EstudianteRequestDto estudiante) {
        try {
            this.estudianteService.update(estudianteId, estudiante);
            return ResponseEntity.ok().build();
        }catch (Exception e) {
            log.error("Error al actualizar estudiante", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
