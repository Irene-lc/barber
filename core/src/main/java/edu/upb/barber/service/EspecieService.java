package edu.upb.barber.service;

import edu.upb.barber.repository.EspecieRepository;
import edu.upb.barber.repository.dto.request.EspecieRequestDto;
import edu.upb.barber.repository.dto.response.EspecieResponseDto;
import edu.upb.barber.repository.entity.Especie;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EspecieService {

    private final EspecieRepository especieRepository;

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
            throw new Exception("El campo nombre es requerido");
        }

        Especie especie = new Especie();
        especie.setNombre(dto.getNombre());
        if (dto.getActivo() != null) {
            especie.setActivo(dto.getActivo());
        }

        return mapToResponse(especieRepository.save(especie));
    }

    @Transactional
    public EspecieResponseDto update(String id, EspecieRequestDto dto) throws Exception {
        Especie especie = especieRepository.findById(id)
                .orElseThrow(() -> new Exception("Especie no encontrada con id: " + id));

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }

        especie.setNombre(dto.getNombre());
        if (dto.getActivo() != null) {
            especie.setActivo(dto.getActivo());
        }

        return mapToResponse(especieRepository.save(especie));
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        if (!especieRepository.existsById(id)) {
            throw new Exception("Especie no encontrada con id: " + id);
        }
        especieRepository.deleteById(id);
    }

    private EspecieResponseDto mapToResponse(Especie especie) {
        return EspecieResponseDto.builder()
                .id(especie.getId())
                .nombre(especie.getNombre())
                .activo(especie.isActivo())
                .build();
    }
}