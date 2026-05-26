package edu.upb.barber.service;

import ch.qos.logback.core.util.StringUtil;
import edu.upb.barber.repository.MateriaRepository;
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

    private final MateriaRepository repository;

    @Transactional
    public void save(Materia materia) throws Exception {
        if (StringUtil.isNullOrEmpty(materia.getNombre())) {
            log.error("El campo nombre es null");
            throw new Exception("El campo nombre es null");
        }

        if (StringUtil.isNullOrEmpty(materia.getSigla())) {
            log.error("El campo sigla es null");
            throw new Exception("El campo sigla es null");
        }
        Materia materia1 = new Materia();
        materia1.setNombre(materia.getNombre());
        materia1.setSigla(materia.getSigla());

        repository.save(materia1);
    }

    @Transactional(readOnly = true)
    public List<Materia> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Materia> findByID(String id) {
        return repository.findById(id);
    }

    @Transactional
    public void update(String materiaId, Materia materia) throws Exception {
        if(StringUtil.isNullOrEmpty(materia.getNombre())) {
            log.error("Error al guardar materia. El campo nombre null");
            throw new Exception("El campo nombre es null");
        }

        if(StringUtil.isNullOrEmpty(materia.getSigla())) {
            log.error("Error al guardar materia. El campo sigla null");
            throw new Exception("El campo sigla es null");
        }

        this.repository.actualizarMateria(materiaId, materia.getNombre(), materia.getSigla());
//        Optional<Materia> optionalMateria = this.repository.findById(materiaId);
//        if (optionalMateria.isEmpty()) {
//            throw new Exception("No existe la materia con el id: " + materiaId);
//        }
//        Materia materia1 = optionalMateria.get();
//        materia1.setNombre(materia.getNombre());
//        materia1.setSigla(materia.getSigla());
//        this.repository.save(materia1);
    }

}