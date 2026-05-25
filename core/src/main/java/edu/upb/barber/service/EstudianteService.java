package edu.upb.barber.service;

import edu.upb.barber.repository.EstudianteRepository;
import edu.upb.barber.repository.MateriaRepository;
import edu.upb.barber.repository.dto.request.EstudianteRequestDto;
import edu.upb.barber.repository.dto.response.EstudianteResponseDto;
import edu.upb.barber.repository.entity.Estudiante;
import edu.upb.barber.repository.entity.Materia;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final MateriaRepository materiaRepository;

    @Transactional(readOnly = true)
    public List<EstudianteResponseDto> listar() {
        return estudianteRepository.findAll().stream()
                .map(EstudianteResponseDto::new)
                .toList();
    }

    @Transactional
    public void save(EstudianteRequestDto dto) throws Exception {
        Estudiante estudiante = new Estudiante();
        estudiante.setNombre(dto.getNombre());
        estudiante.setApellido(dto.getApellido());
        estudiante.setNota(dto.getNota());
        estudiante.setNroTelefono(dto.getNroTelefono());
        estudiante.setNroDocumento(dto.getNroDocumento());

        if (dto.getMateriaId() != null) {
            Materia materia = materiaRepository.findById(dto.getMateriaId())
                    .orElseThrow(() -> new Exception("Materia no encontrada"));
            estudiante.setMateria(materia);
        } else {
            throw new Exception("El ID de la materia no puede ser nulo");
        }

        estudianteRepository.save(estudiante);
    }

    @Transactional
    public void update(String id, EstudianteRequestDto dto) throws Exception {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new Exception("Estudiante no encontrado"));
        
        if (dto.getMateriaId() != null && !dto.getMateriaId().equals(estudiante.getMateria().getId())) {
            Materia materia = materiaRepository.findById(dto.getMateriaId())
                    .orElseThrow(() -> new Exception("Materia no encontrada"));
            estudiante.setMateria(materia);
        }

        estudiante.setNombre(dto.getNombre());
        estudiante.setApellido(dto.getApellido());
        estudiante.setNota(dto.getNota());
        estudiante.setNroTelefono(dto.getNroTelefono());
        estudiante.setNroDocumento(dto.getNroDocumento());

        estudianteRepository.actualizarEstudiante(
                id,
                dto.getNombre(),
                dto.getApellido(),
                dto.getNota(),
                dto.getNroTelefono(),
                dto.getNroDocumento()
        );
    }
}
