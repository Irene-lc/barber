package edu.upb.barber.service;

import edu.upb.barber.repository.ComboServicioDetalleRepository;
import edu.upb.barber.repository.ComboServicioRepository;
import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.ServicioRepository;
import edu.upb.barber.repository.dto.request.ComboServicioDetalleRequestDto;
import edu.upb.barber.repository.dto.request.ComboServicioRequestDto;
import edu.upb.barber.repository.dto.response.ComboServicioDetalleResponseDto;
import edu.upb.barber.repository.dto.response.ComboServicioResponseDto;
import edu.upb.barber.repository.entity.ComboServicio;
import edu.upb.barber.repository.entity.ComboServicioDetalle;
import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.Servicio;
import edu.upb.barber.service.exception.OperationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class ComboServicioService {

    private final ComboServicioRepository comboServicioRepository;
    private final ComboServicioDetalleRepository comboServicioDetalleRepository;
    private final EmpresaRepository empresaRepository;
    private final ServicioRepository servicioRepository;
    private final LogService logService;

    @Transactional(readOnly = true)
    public List<ComboServicioResponseDto> listar() {
        return comboServicioRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ComboServicioResponseDto> findById(String id) {
        return comboServicioRepository.findById(id)
                .map(this::mapToResponse);
    }

    @Transactional
    public ComboServicioResponseDto save(ComboServicioRequestDto dto) throws Exception {

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            log.error("Error al guardar combo. El campo nombre es requerido");
            logService.error("Error al guardar combo. El campo nombre es requerido");
            throw new OperationException("El campo nombre es requerido");
        }
        if (dto.getPrecio() == null || dto.getPrecio().doubleValue() < 0) {
            log.error("Error al guardar combo. El precio debe ser mayor o igual a cero");
            logService.error("Error al guardar combo. El precio debe ser mayor o igual a cero");
            throw new OperationException("El precio debe ser mayor o igual a cero");
        }
        if (dto.getDuracionMinutos() == null || dto.getDuracionMinutos() <= 0) {
            log.error("Error al guardar combo. La duracion debe ser mayor a cero");
            logService.error("Error al guardar combo. La duracion debe ser mayor a cero");
            throw new OperationException("La duracion debe ser mayor a cero");
        }
        if (dto.getEmpresaId() == null || dto.getEmpresaId().isBlank()) {
            log.error("Error al guardar combo. El campo empresa_id es requerido");
            logService.error("Error al guardar combo. El campo empresa_id es requerido");
            throw new OperationException("El campo empresa_id es requerido");
        }

        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new OperationException("Empresa no encontrada con id: " + dto.getEmpresaId()));

        ComboServicio combo = new ComboServicio();
        combo.setEmpresa(empresa);
        combo.setNombre(dto.getNombre());
        combo.setDescripcion(dto.getDescripcion());
        combo.setPrecio(dto.getPrecio());
        combo.setDuracionMinutos(dto.getDuracionMinutos());

        if (dto.getActivo() != null) {
            combo.setActivo(dto.getActivo());
        }

        combo = comboServicioRepository.save(combo);

        if (dto.getDetalles() != null) {
            for (ComboServicioDetalleRequestDto detDto : dto.getDetalles()) {
                Servicio servicio = servicioRepository.findById(detDto.getServicioId())
                        .orElseThrow(() -> new OperationException("Servicio no encontrado con id: " + detDto.getServicioId()));

                ComboServicioDetalle detalle = new ComboServicioDetalle();
                detalle.setComboServicio(combo);
                detalle.setServicio(servicio);
                detalle.setOrdenEjecucion(detDto.getOrdenEjecucion());
                comboServicioDetalleRepository.save(detalle);
            }
        }

        logService.info("ComboServicio guardado exitosamente: " + dto.getNombre());
        return mapToResponse(combo);
    }

    @Transactional
    public ComboServicioResponseDto update(String comboId, ComboServicioRequestDto dto) throws Exception {

        ComboServicio combo = comboServicioRepository.findById(comboId)
                .orElseThrow(() -> new OperationException("ComboServicio no encontrado con id: " + comboId));

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            log.error("Error al actualizar combo. El campo nombre es requerido");
            logService.error("Error al actualizar combo. El campo nombre es requerido");
            throw new OperationException("El campo nombre es requerido");
        }
        if (dto.getPrecio() == null || dto.getPrecio().doubleValue() < 0) {
            log.error("Error al actualizar combo. El precio debe ser mayor o igual a cero");
            logService.error("Error al actualizar combo. El precio debe ser mayor o igual a cero");
            throw new OperationException("El precio debe ser mayor o igual a cero");
        }
        if (dto.getDuracionMinutos() == null || dto.getDuracionMinutos() <= 0) {
            log.error("Error al actualizar combo. La duracion debe ser mayor a cero");
            logService.error("Error al actualizar combo. La duracion debe ser mayor a cero");
            throw new OperationException("La duracion debe ser mayor a cero");
        }

        combo.setNombre(dto.getNombre());
        combo.setDescripcion(dto.getDescripcion());
        combo.setPrecio(dto.getPrecio());
        combo.setDuracionMinutos(dto.getDuracionMinutos());

        if (dto.getActivo() != null) {
            combo.setActivo(dto.getActivo());
        }

        combo = comboServicioRepository.save(combo);

        if (dto.getDetalles() != null) {
            comboServicioDetalleRepository.deleteByComboServicioId(comboId);

            for (ComboServicioDetalleRequestDto detDto : dto.getDetalles()) {
                Servicio servicio = servicioRepository.findById(detDto.getServicioId())
                        .orElseThrow(() -> new OperationException("Servicio no encontrado con id: " + detDto.getServicioId()));

                ComboServicioDetalle detalle = new ComboServicioDetalle();
                detalle.setComboServicio(combo);
                detalle.setServicio(servicio);
                detalle.setOrdenEjecucion(detDto.getOrdenEjecucion());
                comboServicioDetalleRepository.save(detalle);
            }
        }

        logService.info("ComboServicio actualizado exitosamente: " + comboId);
        return mapToResponse(combo);
    }

    @Transactional
    public void delete(String id) throws Exception {
        if (!comboServicioRepository.existsById(id)) {
            log.error("Error al eliminar combo. No encontrado con id: {}", id);
            logService.error("Error al eliminar combo. No encontrado con id: " + id);
            throw new OperationException("ComboServicio no encontrado con id: " + id);
        }
        comboServicioDetalleRepository.deleteByComboServicioId(id);
        comboServicioRepository.deleteById(id);
        logService.info("ComboServicio eliminado exitosamente: " + id);
    }

    private ComboServicioResponseDto mapToResponse(ComboServicio combo) {
        ComboServicioResponseDto res = new ComboServicioResponseDto(combo);
        List<ComboServicioDetalle> detalles = comboServicioDetalleRepository.findByComboServicioId(combo.getId());
        res.setDetalles(detalles.stream()
                .map(ComboServicioDetalleResponseDto::new)
                .collect(Collectors.toList()));
        return res;
    }
}
