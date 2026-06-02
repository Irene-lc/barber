package edu.upb.barber.service;

import edu.upb.barber.repository.EmpleadoRepository;
import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.UsuarioRepository;
import edu.upb.barber.repository.dto.request.EmpleadoRequestDto;
import edu.upb.barber.repository.dto.response.EmpleadoResponseDto;
import edu.upb.barber.repository.entity.Empleado;
import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.Usuario;
import edu.upb.barber.repository.entity.enums.CargoEmpleado;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<EmpleadoResponseDto> listar() {
        return empleadoRepository.findAll().stream()
                .map(EmpleadoResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<EmpleadoResponseDto> findById(String id) {
        return empleadoRepository.findById(id)
                .map(EmpleadoResponseDto::new);
    }

    @Transactional
    public EmpleadoResponseDto save(EmpleadoRequestDto dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }
        if (dto.getEmpresaId() == null || dto.getEmpresaId().isBlank()) {
            throw new Exception("El campo empresa_id es requerido");
        }

        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new Exception("Empresa no encontrada con id: " + dto.getEmpresaId()));

        Empleado empleado = new Empleado();
        empleado.setNombre(dto.getNombre());
        empleado.setTelefono(dto.getTelefono());
        empleado.setEmail(dto.getEmail());
        empleado.setEspecialidad(dto.getEspecialidad());
        empleado.setFotoUrl(dto.getFotoUrl());
        empleado.setEmpresa(empresa);

        if (dto.getUsuarioId() != null && !dto.getUsuarioId().isBlank()) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new Exception("Usuario no encontrado con id: " + dto.getUsuarioId()));
            empleado.setUsuario(usuario);
        }

        if (dto.getCargo() != null) {
            empleado.setCargo(CargoEmpleado.valueOf(dto.getCargo()));
        }
        if (dto.getDisponible() != null) {
            empleado.setDisponible(dto.getDisponible());
        }
        if (dto.getActivo() != null) {
            empleado.setActivo(dto.getActivo());
        }

        return new EmpleadoResponseDto(empleadoRepository.save(empleado));
    }

    @Transactional
    public EmpleadoResponseDto update(String empleadoId, EmpleadoRequestDto dto) throws Exception {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new Exception("Empleado no encontrado con id: " + empleadoId));

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }

        empleado.setNombre(dto.getNombre());
        empleado.setTelefono(dto.getTelefono());
        empleado.setEmail(dto.getEmail());
        empleado.setEspecialidad(dto.getEspecialidad());
        empleado.setFotoUrl(dto.getFotoUrl());

        if (dto.getEmpresaId() != null && !dto.getEmpresaId().isBlank()) {
            Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new Exception("Empresa no encontrada con id: " + dto.getEmpresaId()));
            empleado.setEmpresa(empresa);
        }

        if (dto.getUsuarioId() != null && !dto.getUsuarioId().isBlank()) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new Exception("Usuario no encontrado con id: " + dto.getUsuarioId()));
            empleado.setUsuario(usuario);
        } else {
            empleado.setUsuario(null);
        }

        if (dto.getCargo() != null) {
            empleado.setCargo(CargoEmpleado.valueOf(dto.getCargo()));
        } else {
            empleado.setCargo(null);
        }

        if (dto.getDisponible() != null) {
            empleado.setDisponible(dto.getDisponible());
        }
        if (dto.getActivo() != null) {
            empleado.setActivo(dto.getActivo());
        }

        return new EmpleadoResponseDto(empleadoRepository.save(empleado));
    }

    @Transactional
    public void delete(String id) throws Exception {
        if (!empleadoRepository.existsById(id)) {
            throw new Exception("Empleado no encontrado con id: " + id);
        }
        empleadoRepository.deleteById(id);
    }
}
