package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.EmpresaRequestDto;
import edu.upb.barber.repository.dto.response.EmpresaResponseDto;
import edu.upb.barber.repository.entity.Usuario;
import edu.upb.barber.service.EmpresaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

//    @Secured({"SUPER_ADMIN", "ADMIN_EMPREESA"})
    @GetMapping
    public ResponseEntity<List<EmpresaResponseDto>> empresas() {

        Usuario user = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Usuario autenticado: {}", user.getRol());

        try {

            return ResponseEntity.ok(
                    empresaService.listar());

        } catch (Exception e) {

            log.error(
                    "Error al listar empresas", e);

            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }

    @PostMapping
    public ResponseEntity<?> guardar(
            @RequestBody EmpresaRequestDto empresa
    ) {
        try {
            empresaService.save(empresa);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(
                    "Error al guardar empresa", e);
            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable("id") String empresaId,
                                           @RequestBody EmpresaRequestDto empresa) {
        try {
            this.empresaService.update(empresaId, empresa);
            return ResponseEntity.ok().build();
        }catch (Exception e) {
            log.error("Error al actualizar empresa", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponseDto> obtenerPorId(@PathVariable("id") String empresaId) {
        try {
            return empresaService.findById(empresaId)
                    .map(EmpresaResponseDto::new)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener Empresa", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") String empresaId) {
        try {
            empresaService.delete(empresaId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar Empresa", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}