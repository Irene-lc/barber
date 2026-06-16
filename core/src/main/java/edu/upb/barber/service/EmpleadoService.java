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
import edu.upb.barber.service.exception.OperationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@AllArgsConstructor
@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final LogService logService;

    @Transactional(readOnly = true)
    public List<EmpleadoResponseDto> listar() {
        Usuario currentUser = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
            SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Usuario) {
            currentUser = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }

        String empresaId = (currentUser != null && currentUser.getEmpresa() != null)
                ? currentUser.getEmpresa().getId()
                : null;

        List<Empleado> empleados = (empresaId != null)
                ? empleadoRepository.findByEmpresaId(empresaId)
                : empleadoRepository.findAll();

        return empleados.stream()
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
            log.error("Error al guardar empleado. El campo nombre es requerido");
            logService.error("Error al guardar empleado. El campo nombre es requerido");
            throw new OperationException("El campo nombre es requerido");
        }
        if (dto.getEmpresaId() == null || dto.getEmpresaId().isBlank()) {
            log.error("Error al guardar empleado. El campo empresa_id es requerido");
            logService.error("Error al guardar empleado. El campo empresa_id es requerido");
            throw new OperationException("El campo empresa_id es requerido");
        }

        Usuario currentUser = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
            SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Usuario) {
            currentUser = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }

        if (currentUser != null && currentUser.getEmpresa() != null) {
            if (!currentUser.getEmpresa().getId().equals(dto.getEmpresaId())) {
                log.error("Error al guardar empleado. Sin permiso para operar en empresa: {}", dto.getEmpresaId());
                logService.error("Error al guardar empleado. Sin permiso para operar en empresa: " + dto.getEmpresaId());
                throw new OperationException("No tienes permiso para operar en la empresa especificada");
            }
        }

        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new OperationException("Empresa no encontrada con id: " + dto.getEmpresaId()));

        Empleado empleado = new Empleado();
        empleado.setNombre(dto.getNombre());
        empleado.setTelefono(dto.getTelefono());
        empleado.setEspecialidad(dto.getEspecialidad());
        empleado.setFotoUrl(dto.getFotoUrl());
        empleado.setEmpresa(empresa);

        if (dto.getUsuarioId() != null && !dto.getUsuarioId().isBlank()) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new OperationException("Usuario no encontrado con id: " + dto.getUsuarioId()));
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

        logService.info("Empleado guardado exitosamente: " + dto.getNombre());
        return new EmpleadoResponseDto(empleadoRepository.save(empleado));
    }

    @Transactional
    public EmpleadoResponseDto update(String empleadoId, EmpleadoRequestDto dto) throws Exception {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new OperationException("Empleado no encontrado con id: " + empleadoId));

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            log.error("Error al actualizar empleado. El campo nombre es requerido");
            logService.error("Error al actualizar empleado. El campo nombre es requerido");
            throw new OperationException("El campo nombre es requerido");
        }

        empleado.setNombre(dto.getNombre());
        empleado.setTelefono(dto.getTelefono());
        empleado.setEspecialidad(dto.getEspecialidad());
        empleado.setFotoUrl(dto.getFotoUrl());

        if (dto.getEmpresaId() != null && !dto.getEmpresaId().isBlank()) {
            Usuario currentUser = null;
            if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Usuario) {
                currentUser = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            }

            if (currentUser != null && currentUser.getEmpresa() != null) {
                if (!currentUser.getEmpresa().getId().equals(dto.getEmpresaId())) {
                    log.error("Error al actualizar empleado. Sin permiso para operar en empresa: {}", dto.getEmpresaId());
                    logService.error("Error al actualizar empleado. Sin permiso para operar en empresa: " + dto.getEmpresaId());
                    throw new OperationException("No tienes permiso para operar en la empresa especificada");
                }
            }

            Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new OperationException("Empresa no encontrada con id: " + dto.getEmpresaId()));
            empleado.setEmpresa(empresa);
        }

        if (dto.getUsuarioId() != null && !dto.getUsuarioId().isBlank()) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new OperationException("Usuario no encontrado con id: " + dto.getUsuarioId()));
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

        logService.info("Empleado actualizado exitosamente: " + empleadoId);
        return new EmpleadoResponseDto(empleadoRepository.save(empleado));
    }

    @Transactional
    public void delete(String id) throws Exception {
        if (!empleadoRepository.existsById(id)) {
            log.error("Error al eliminar empleado. No encontrado con id: {}", id);
            logService.error("Error al eliminar empleado. No encontrado con id: " + id);
            throw new OperationException("Empleado no encontrado con id: " + id);
        }
        empleadoRepository.deleteById(id);
        logService.info("Empleado eliminado exitosamente: " + id);
    }
}
