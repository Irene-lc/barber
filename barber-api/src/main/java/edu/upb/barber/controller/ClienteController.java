package edu.upb.barber.controller;

import edu.upb.barber.repository.dto.request.ClienteRequestDto;
import edu.upb.barber.repository.dto.response.ClienteResponseDto;
import edu.upb.barber.repository.entity.Cliente;
import edu.upb.barber.repository.entity.Log;
import edu.upb.barber.service.ClienteService;
import edu.upb.barber.service.LogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteResponseDto>> listar() {
        try {
            return ResponseEntity.ok(clienteService.listar());
        } catch (Exception e) {
            log.error("Error al listar clientes", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> obtenerPorId(@PathVariable String id) {
        try {
            return clienteService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener Cliente", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDto> guardar(@RequestBody ClienteRequestDto dto) {
        try {
            return ResponseEntity.ok(clienteService.save(dto));
        } catch (Exception e) {
            log.error("Error al guardar Cliente", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> actualizar(
            @PathVariable("id") String clienteId,
            @RequestBody ClienteRequestDto dto
    ) {
        try {
            return ResponseEntity.ok(clienteService.update(clienteId, dto));
        } catch (Exception e) {
            log.error("Error al actualizar Cliente", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            clienteService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar Cliente", e);
            return ResponseEntity.internalServerError().build();
        }
    }



    @GetMapping("/page")
    public ResponseEntity<Page<ClienteResponseDto>> cliente(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                              @RequestParam(value = "size", defaultValue = "10") Integer size,
                                              @RequestParam(value = "sortBy", defaultValue = "createdDate") String sortBy,
                                              @RequestParam(value = "sortDir", defaultValue = "DESC") Sort.Direction sortDir,

                                              @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
                                              @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) {

        try {
            return ResponseEntity.ok(clienteService.findAllByOderByDataDesc(from.toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    to.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),

                    PageRequest.of(page, size, Sort.by(sortDir, sortBy)))
            );
        } catch (Exception e) {
            log.error("Error al listar el inventario de activos", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
