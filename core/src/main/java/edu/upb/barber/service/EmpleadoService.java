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

import edu.upb.barber.repository.EmpleadoSucursalRepository;
import edu.upb.barber.repository.SucursalRepository;
import edu.upb.barber.repository.entity.EmpleadoSucursal;
import edu.upb.barber.repository.entity.Sucursal;
import edu.upb.barber.repository.entity.enums.RolUsuario;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private final EmpleadoSucursalRepository empleadoSucursalRepository;
    private final SucursalRepository sucursalRepository;
    private final PasswordEncoder passwordEncoder;
    private final LogService logService;

    @Transactional(readOnly = true)
    public List<EmpleadoResponseDto> listar() {
        Usuario currentUser = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
            SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Usuario) {
            currentUser = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }

        String empresaId = (currentUser != null && currentUser.getEmpresa() != null && currentUser.getRol() != RolUsuario.ROLE_CLIENTE)
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

        Usuario usuario = null;
        if ((dto.getUsuarioId() == null || dto.getUsuarioId().isBlank()) && dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (usuarioRepository.findByEmail(dto.getEmail().trim()).isPresent()) {
                throw new OperationException("Ya existe un usuario registrado con el email: " + dto.getEmail());
            }

            String nombreEmp = dto.getNombre().trim();
            String apellidoEmp = "";
            int primerEspacio = nombreEmp.indexOf(" ");
            if (primerEspacio != -1) {
                apellidoEmp = nombreEmp.substring(primerEspacio + 1).trim();
                nombreEmp = nombreEmp.substring(0, primerEspacio).trim();
            }

            usuario = Usuario.builder()
                    .nombre(nombreEmp)
                    .apellido(apellidoEmp)
                    .email(dto.getEmail().trim())
                    .passwordHash(passwordEncoder.encode("Abc123**"))
                    .rol(RolUsuario.ROLE_EMPLEADO)
                    .empresa(empresa)
                    .activo(true)
                    .build();

            usuario = usuarioRepository.save(usuario);
        } else if (dto.getUsuarioId() != null && !dto.getUsuarioId().isBlank()) {
            usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new OperationException("Usuario no encontrado con id: " + dto.getUsuarioId()));
        }

        Empleado empleado = new Empleado();
        empleado.setNombre(dto.getNombre());
        empleado.setTelefono(dto.getTelefono());
        empleado.setEspecialidad(dto.getEspecialidad());
        if (dto.getFotoUrl() != null) {
            empleado.setFotoUrl(dto.getFotoUrl().isBlank() ? null : dto.getFotoUrl());
        }
        empleado.setEmpresa(empresa);
        if (usuario != null) {
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

        Empleado guardado = empleadoRepository.save(empleado);

        if (dto.getSucursalId() != null && !dto.getSucursalId().isBlank()) {
            Sucursal sucursal = sucursalRepository.findById(dto.getSucursalId())
                    .orElseThrow(() -> new OperationException("Sucursal no encontrada con id: " + dto.getSucursalId()));

            EmpleadoSucursal es = new EmpleadoSucursal();
            es.setEmpleado(guardado);
            es.setSucursal(sucursal);
            es.setActivo(true);
            empleadoSucursalRepository.save(es);
        }

        logService.info("Empleado guardado exitosamente: " + dto.getNombre());
        return new EmpleadoResponseDto(guardado);
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
        if (dto.getFotoUrl() != null) {
            empleado.setFotoUrl(dto.getFotoUrl().isBlank() ? null : dto.getFotoUrl());
        }

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
