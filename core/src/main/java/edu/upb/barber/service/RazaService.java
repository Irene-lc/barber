package edu.upb.barber.service;

import edu.upb.barber.repository.EspecieRepository;
import edu.upb.barber.repository.RazaRepository;
import edu.upb.barber.repository.dto.request.RazaRequestDto;
import edu.upb.barber.repository.dto.response.RazaResponseDto;
import edu.upb.barber.repository.entity.Especie;
import edu.upb.barber.repository.entity.Raza;
import edu.upb.barber.service.exception.OperationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class RazaService {

    private final RazaRepository razaRepository;
    private final EspecieRepository especieRepository;
    private final LogService logService;

    @Transactional(readOnly = true)
    public List<RazaResponseDto> listar() {
        return razaRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<RazaResponseDto> obtenerPorId(String id) {
        return razaRepository.findById(id)
                .map(this::mapToResponse);
    }

    @Transactional
    public RazaResponseDto guardar(RazaRequestDto dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            log.error("Error al guardar raza. El campo nombre es requerido");
            logService.error("Error al guardar raza. El campo nombre es requerido");
            throw new OperationException("El campo nombre es requerido");
        }
        if (dto.getEspecieId() == null || dto.getEspecieId().isBlank()) {
            log.error("Error al guardar raza. El campo especie_id es requerido");
            logService.error("Error al guardar raza. El campo especie_id es requerido");
            throw new OperationException("El campo especie_id es requerido");
        }

        Especie especie = especieRepository.findById(dto.getEspecieId())
                .orElseThrow(() -> new OperationException("Especie no encontrada con id: " + dto.getEspecieId()));

        Raza raza = new Raza();
        raza.setNombre(dto.getNombre());
        raza.setEspecie(especie);
        if (dto.getActivo() != null) {
            raza.setActivo(dto.getActivo());
        }

        logService.info("Raza guardada exitosamente: " + dto.getNombre());
        return mapToResponse(razaRepository.save(raza));
    }

    @Transactional
    public RazaResponseDto update(String id, RazaRequestDto dto) throws Exception {
        Raza raza = razaRepository.findById(id)
                .orElseThrow(() -> new OperationException("Raza no encontrada con id: " + id));

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            log.error("Error al actualizar raza. El campo nombre es requerido");
            logService.error("Error al actualizar raza. El campo nombre es requerido");
            throw new OperationException("El campo nombre es requerido");
        }

        raza.setNombre(dto.getNombre());

        if (dto.getEspecieId() != null && !dto.getEspecieId().isBlank()) {
            Especie especie = especieRepository.findById(dto.getEspecieId())
                    .orElseThrow(() -> new OperationException("Especie no encontrada con id: " + dto.getEspecieId()));
            raza.setEspecie(especie);
        }

        if (dto.getActivo() != null) {
            raza.setActivo(dto.getActivo());
        }

        logService.info("Raza actualizada exitosamente: " + id);
        return mapToResponse(razaRepository.save(raza));
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        if (!razaRepository.existsById(id)) {
            log.error("Error al eliminar raza. No encontrada con id: {}", id);
            logService.error("Error al eliminar raza. No encontrada con id: " + id);
            throw new OperationException("Raza no encontrada con id: " + id);
        }
        razaRepository.deleteById(id);
        logService.info("Raza eliminada exitosamente: " + id);
    }

    private RazaResponseDto mapToResponse(Raza raza) {
        return RazaResponseDto.builder()
                .id(raza.getId())
                .nombre(raza.getNombre())
                .especieId(raza.getEspecie() != null ? raza.getEspecie().getId() : null)
                .activo(raza.isActivo())
                .build();
    }
}
