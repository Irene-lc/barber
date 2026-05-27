package edu.upb.barber.service;

import edu.upb.barber.repository.EmpleadoRepository;
import edu.upb.barber.repository.entity.Empleado;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    @Transactional(readOnly = true)
    public List<Empleado> listar() {
        return empleadoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Empleado> findById(String id) {
        return empleadoRepository.findById(id);
    }

    @Transactional
    public void save(Empleado empleado) {
        empleadoRepository.save(empleado);
    }

    @Transactional
    public void delete(String id) {
        empleadoRepository.deleteById(id);
    }
}
