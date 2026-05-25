package edu.upb.barber.service;

import ch.qos.logback.core.util.StringUtil;
import edu.upb.barber.repository.MateriaRepository;
import edu.upb.barber.repository.dto.request.MateriaRequestDto;
import edu.upb.barber.repository.dto.response.MateriaResponseDto;
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
public class MateriaService {

    private final MateriaRepository materiaRepository;

    @Transactional
    public void save(MateriaRequestDto materiaRequestDto) throws Exception {

        if (StringUtil.isNullOrEmpty(materiaRequestDto.getNombre())) {
            log.error("El campo nombre es null");
            throw new Exception("El campo nombre es null");
        }

        Optional<Materia> materiaExistente = materiaRepository.findBySigla(materiaRequestDto.getSigla());

        if (materiaExistente.isPresent()) {
            log.error("Ya existe una materia con esa sigla");
            throw new Exception("Ya existe una materia con esa sigla");
        }

        Materia materia = new Materia();
        materia.setNombre(materiaRequestDto.getNombre());
        materia.setSigla(materiaRequestDto.getSigla());

        materiaRepository.save(materia);
    }

    @Transactional(readOnly = true)
    public List<MateriaResponseDto> listar() {
        return materiaRepository.findAll()
                .stream()
                .map(MateriaResponseDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<Materia> findById(String id) {
        return materiaRepository.findById(id);
    }

    @Transactional
    public void update(String materiaId, MateriaRequestDto materia) throws Exception {
        if(StringUtil.isNullOrEmpty(materia.getNombre())) {
            log.error("Error al guardar materia. El campo nombre null");
            throw new Exception("El campo nombre es null");
        }

        if(StringUtil.isNullOrEmpty(materia.getSigla())) {
            log.error("Error al guardar materia. El campo sigla null");
            throw new Exception("El campo sigla es null");
        }

        this.materiaRepository.actualizarMateria(materiaId, materia.getNombre(), materia.getSigla());
        
        Optional<Materia> optionalMateria = this.materiaRepository.findById(materiaId);
        if (optionalMateria.isEmpty()) {
            throw new Exception("No existe la materia con el id: " + materiaId);
        }
    }
}
