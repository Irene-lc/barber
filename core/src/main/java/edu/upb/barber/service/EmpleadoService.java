package edu.upb.barber.service;

import edu.upb.barber.repository.EmpleadoRepository;
import edu.upb.barber.repository.dto.request.EmpleadoRequestDto;
import edu.upb.barber.repository.entity.Empleado;
import edu.upb.barber.repository.entity.enums.CargoEmpleado;
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
    @Transactional
    public void update(String empleadoId, EmpleadoRequestDto dto) throws Exception {

        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new Exception("Empleado no encontrado con id: " + empleadoId));

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }

        empleado.setNombre(dto.getNombre());
        empleado.setTelefono(dto.getTelefono());
        empleado.setEmail(dto.getEmail());

        if (dto.getCargo() != null) {
            empleado.setCargo(CargoEmpleado.valueOf(dto.getCargo()));
        }

        empleadoRepository.save(empleado);
    }

}
