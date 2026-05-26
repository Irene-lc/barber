package edu.upb.barber.service;

import ch.qos.logback.core.util.StringUtil;
import edu.upb.barber.repository.EstudianteRepository;
import edu.upb.barber.repository.MateriaRepository;
import edu.upb.barber.repository.dto.request.EstudianteRequestDto;
import edu.upb.barber.repository.entity.Estudiante;
import edu.upb.barber.repository.entity.Materia;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class EstudianteService {

    private final MateriaService service;
    private final EstudianteRepository repository;

    @Transactional
    public void guardar(String materiaId, Estudiante estudiante) {
        Optional<Materia> optionalMateria = this.service.findByID(materiaId);
        Materia materia = null;
        if (optionalMateria.isPresent()) {
            materia = optionalMateria.get();
        }
        estudiante.setMateria(materia);
        this.repository.save(estudiante);
    }

    @Transactional
    public void save(Estudiante estudiante) throws Exception {
        if (StringUtil.isNullOrEmpty(estudiante.getNombre())) {
            log.error("El campo nombre es null");
            throw new Exception("El campo nombre es null");
        }

        if (StringUtil.isNullOrEmpty(estudiante.getApellido())) {
            log.error("El campo apellido es null");
            throw new Exception("El campo apellido es null");
        }
        if (StringUtil.isNullOrEmpty(estudiante.getMateria().getNombre())) {
            log.error("El campo materia es null");
            throw new Exception("El campo materia es null");
        }
        if (StringUtil.isNullOrEmpty(estudiante.getNota().toString())) {
            log.error("El campo nota es null");
            throw new Exception("El campo nota es null");
        }
        Estudiante estudiante1 = new Estudiante();
        estudiante1.setNombre(estudiante.getNombre());
        estudiante1.setApellido(estudiante.getApellido());
        estudiante1.setMateria(estudiante.getMateria());
        estudiante1.setNota(estudiante.getNota());
        estudiante1.setNroTelefono(estudiante.getNroTelefono());
        estudiante1.setNroDocumento(estudiante.getNroDocumento());

        repository.save(estudiante1);
    }

    @Transactional(readOnly = true)
    public List<Estudiante> listar() {
        return repository.listar();
    }

    @Transactional(readOnly = true)
    public Optional<Estudiante> findById(String id) {
        return repository.findById(id);
    }

    @Transactional
    public void update(String estudianteId, EstudianteRequestDto estudiante) throws Exception {
        if (StringUtil.isNullOrEmpty(estudiante.getNroDocumento())) {
            log.error("El campo nro documento es null");
            throw new Exception("El campo nro documento es null");
        }

        if (StringUtil.isNullOrEmpty(estudiante.getNroTelefono())) {
            log.error("El campo telefono es null");
            throw new Exception("El campo telefono es null");
        }

        this.repository.actualizarEstudiante(estudianteId, estudiante.getNroTelefono(), estudiante.getNroDocumento());
//        Optional<Estudiante> optionalEstudiante = this.repository.findById(estudianteId);
//        if (optionalEstudiante.isEmpty()) {
//            throw new Exception("No existe la materia con el id: " + estudianteId);
//        }
//        Estudiante estudiante1 = optionalEstudiante.get();
//        estudiante1.setNroDocumento(estudiante.getNroDocumento());
//        estudiante1.setNroTelefono(estudiante.getNroTelefono());
//        this.repository.save(estudiante1);
    }
}
