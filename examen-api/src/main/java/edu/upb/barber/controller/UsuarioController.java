package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.UsuarioRequestDto;
import edu.upb.barber.repository.dto.response.UsuarioResponseDto;
import edu.upb.barber.service.UsuarioService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDto>> usuarios() {
        try {
            return ResponseEntity.ok(usuarioService.listar());
        } catch (Exception e) {
            log.error("Error al listar usuarios", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN_EMPRESA')")
    public ResponseEntity<Void> guardar(
            @RequestBody UsuarioRequestDto usuario
    ) {
        try {
            usuarioService.save(usuario);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al guardar usuario", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable("id") String usuarioId,
                                           @RequestBody UsuarioRequestDto usuario) {
        try {
            this.usuarioService.update(usuarioId, usuario);
            return ResponseEntity.ok().build();
        }catch (Exception e) {
            log.error("Error al actualizar usuario", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}