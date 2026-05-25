package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.MateriaRequestDto;
import edu.upb.barber.repository.dto.response.MateriaResponseDto;
import edu.upb.barber.service.MateriaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/materia")
public class MateriaController {
    private final MateriaService materiaService;

    @GetMapping
    public ResponseEntity<List<MateriaResponseDto>> materias() {
        try {
            return ResponseEntity.ok(materiaService.listar());
        } catch (Exception e) {
            log.error("Error al listar materias", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> guardar(
            @RequestBody MateriaRequestDto materia
    ) {
        try {
            materiaService.save(materia);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al guardar materia", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable("id") String materiaId,
                                           @RequestBody MateriaRequestDto materia) {
        try {
            this.materiaService.update(materiaId, materia);
            return ResponseEntity.ok().build();
        }catch (Exception e) {
            log.error("Error al actualizar materia", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
