package edu.upb.barber.service;

import edu.upb.barber.repository.EspecieRepository;
import edu.upb.barber.repository.RazaRepository;
import edu.upb.barber.repository.dto.request.RazaRequestDto;
import edu.upb.barber.repository.dto.response.RazaResponseDto;
import edu.upb.barber.repository.entity.Especie;
import edu.upb.barber.repository.entity.Raza;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RazaService {

    private final RazaRepository razaRepository;
    private final EspecieRepository especieRepository;

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
            throw new Exception("El campo nombre es requerido");
        }
        if (dto.getEspecieId() == null || dto.getEspecieId().isBlank()) {
            throw new Exception("El campo especie_id es requerido");
        }

        Especie especie = especieRepository.findById(dto.getEspecieId())
                .orElseThrow(() -> new Exception("Especie no encontrada con id: " + dto.getEspecieId()));

        Raza raza = new Raza();
        raza.setNombre(dto.getNombre());
        raza.setEspecie(especie);
        if (dto.getActivo() != null) {
            raza.setActivo(dto.getActivo());
        }

        return mapToResponse(razaRepository.save(raza));
    }

    @Transactional
    public RazaResponseDto update(String id, RazaRequestDto dto) throws Exception {
        Raza raza = razaRepository.findById(id)
                .orElseThrow(() -> new Exception("Raza no encontrada con id: " + id));

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }

        raza.setNombre(dto.getNombre());

        if (dto.getEspecieId() != null && !dto.getEspecieId().isBlank()) {
            Especie especie = especieRepository.findById(dto.getEspecieId())
                    .orElseThrow(() -> new Exception("Especie no encontrada con id: " + dto.getEspecieId()));
            raza.setEspecie(especie);
        }

        if (dto.getActivo() != null) {
            raza.setActivo(dto.getActivo());
        }

        return mapToResponse(razaRepository.save(raza));
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        if (!razaRepository.existsById(id)) {
            throw new Exception("Raza no encontrada con id: " + id);
        }
        razaRepository.deleteById(id);
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