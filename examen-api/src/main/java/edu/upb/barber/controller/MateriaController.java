package edu.upb.barber.controller;

import edu.upb.barber.repository.entity.Materia;
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
@RequestMapping("/api/materias")
public class MateriaController {
    private final MateriaService service;


    @GetMapping()
    public ResponseEntity<List<Materia>> materias() {
        try {
            return ResponseEntity.ok(service.listar());
        }catch (Exception e) {
            log.error("Error al listar materia", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> guardar(@RequestBody Materia materia) {
        try {
            this.service.save(materia);
            return ResponseEntity.ok().build();
        }catch (Exception e) {
            log.error("Error al guardar materia", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable("id")String materiaId,
                                           @RequestBody Materia materia) {
        try {
            this.service.update(materiaId, materia);
            return ResponseEntity.ok().build();
        }catch (Exception e) {
            log.error("Error al actualizar materia", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
