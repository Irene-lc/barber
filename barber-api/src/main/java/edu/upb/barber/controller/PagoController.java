package edu.upb.barber.controller;

import edu.upb.barber.repository.PagoRepository;
import edu.upb.barber.repository.dto.request.GenerarPagoRequestDto;
import edu.upb.barber.repository.dto.request.PagoRequestDto;
import edu.upb.barber.repository.dto.response.GenerarPagoResponseDto;
import edu.upb.barber.repository.dto.response.PagoResponseDto;
import edu.upb.barber.repository.dto.response.PagoResponseDtoTest;
import edu.upb.barber.repository.entity.enums.EstadoPago;
import edu.upb.barber.service.PagoService;
import edu.upb.barber.service.exception.OperationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {

    private final PagoService pagoService;
    private final PagoRepository pagoRepository;

    @PostMapping("/generar-qr")
    public ResponseEntity<GenerarPagoResponseDto> generarCobroQR(@RequestBody GenerarPagoRequestDto request) {
        try {
            GenerarPagoResponseDto response = pagoService.generarCobroQR(request);
            return ResponseEntity.ok(response);
        } catch (OperationException e) {
            log.error("Error al generar cobro QR. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al generar cobro QR", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<PagoResponseDto> crear(@RequestBody PagoRequestDto request) {
        try {
            return ResponseEntity.ok(pagoService.crear(request));
        } catch (OperationException e) {
            log.error("Error al crear Pago. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al crear Pago", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagoResponseDto> actualizar(@PathVariable String id, @RequestBody PagoRequestDto request) {
        try {
            return ResponseEntity.ok(pagoService.update(id, request));
        } catch (OperationException e) {
            log.error("Error al actualizar Pago. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al actualizar Pago", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            pagoService.delete(id);
            return ResponseEntity.ok().build();
        } catch (OperationException e) {
            log.error("Error al eliminar Pago. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al eliminar Pago", e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<PagoResponseDto> obtenerPorId(@PathVariable String id) {
        try {
            return pagoService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener Pago", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping
    public ResponseEntity<List<PagoResponseDto>> listar() {
        try {
            return ResponseEntity.ok(pagoService.listar());
        } catch (Exception e) {
            log.error("Error al listar Pagos", e);
            return ResponseEntity.internalServerError().build();
        }
    }

//    @GetMapping("/paginado")
//    public ResponseEntity<Page<PagoResponseDtoTest>> listarPaginado(
//            @RequestParam(required = false) String dato,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<PagoResponseDtoTest> resultado = pagoService.listar(dato, pageable);
//        return ResponseEntity.ok(resultado);
//    }

    @PatchMapping("/{id}/estado")
    @Transactional
    public ResponseEntity<?> actualizarEstado(
            @PathVariable String id,
            @RequestParam EstadoPago estado) {
        int rows = pagoRepository.actualizarEstadoPago(id, estado);
        if (rows == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("message", "Estado actualizado"));
    }




}
