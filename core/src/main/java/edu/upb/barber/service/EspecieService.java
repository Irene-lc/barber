package edu.upb.barber.service;

import edu.upb.barber.repository.EspecieRepository;
import edu.upb.barber.repository.dto.request.EspecieRequestDto;
import edu.upb.barber.repository.dto.response.EspecieResponseDto;
import edu.upb.barber.repository.entity.Especie;
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
public class EspecieService {

    private final EspecieRepository especieRepository;
    private final LogService logService;

    @Transactional(readOnly = true)
    public List<EspecieResponseDto> listar() {
        return especieRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<EspecieResponseDto> obtenerPorId(String id) {
        return especieRepository.findById(id)
                .map(this::mapToResponse);
    }

    @Transactional
    public EspecieResponseDto guardar(EspecieRequestDto dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            log.error("Error al guardar especie. El campo nombre es requerido");
            logService.error("Error al guardar especie. El campo nombre es requerido");
            throw new OperationException("El campo nombre es requerido");
        }

        Especie especie = new Especie();
        especie.setNombre(dto.getNombre());
        if (dto.getActivo() != null) {
            especie.setActivo(dto.getActivo());
        }

        logService.info("Especie guardada exitosamente: " + dto.getNombre());
        return mapToResponse(especieRepository.save(especie));
    }

    @Transactional
    public EspecieResponseDto update(String id, EspecieRequestDto dto) throws Exception {
        Especie especie = especieRepository.findById(id)
                .orElseThrow(() -> new OperationException("Especie no encontrada con id: " + id));

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            log.error("Error al actualizar especie. El campo nombre es requerido");
            logService.error("Error al actualizar especie. El campo nombre es requerido");
            throw new OperationException("El campo nombre es requerido");
        }

        especie.setNombre(dto.getNombre());
        if (dto.getActivo() != null) {
            especie.setActivo(dto.getActivo());
        }

        logService.info("Especie actualizada exitosamente: " + id);
        return mapToResponse(especieRepository.save(especie));
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        if (!especieRepository.existsById(id)) {
            log.error("Error al eliminar especie. No encontrada con id: {}", id);
            logService.error("Error al eliminar especie. No encontrada con id: " + id);
            throw new OperationException("Especie no encontrada con id: " + id);
        }
        especieRepository.deleteById(id);
        logService.info("Especie eliminada exitosamente: " + id);
    }

    private EspecieResponseDto mapToResponse(Especie especie) {
        return EspecieResponseDto.builder()
                .id(especie.getId())
                .nombre(especie.getNombre())
                .activo(especie.isActivo())
                .build();
    }
}
