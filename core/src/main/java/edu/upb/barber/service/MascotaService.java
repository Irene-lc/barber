package edu.upb.barber.service;

import edu.upb.barber.repository.ClienteRepository;
import edu.upb.barber.repository.MascotaRepository;
import edu.upb.barber.repository.RazaRepository;
import edu.upb.barber.repository.dto.request.MascotaRequestDto;
import edu.upb.barber.repository.dto.response.MascotaResponseDto;
import edu.upb.barber.repository.entity.Cliente;
import edu.upb.barber.repository.entity.Mascota;
import edu.upb.barber.repository.entity.Raza;
import edu.upb.barber.repository.entity.Usuario;
import edu.upb.barber.repository.entity.enums.RolUsuario;
import edu.upb.barber.service.exception.OperationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final RazaRepository razaRepository;
    private final ClienteRepository clienteRepository;
    private final LogService logService;

    @Transactional(readOnly = true)
    public List<MascotaResponseDto> listar() {
        Usuario currentUser = getCurrentUser();
        List<Mascota> mascotas;
        if (currentUser != null && currentUser.getRol() == RolUsuario.ROLE_CLIENTE) {
            List<String> clienteIds = clienteRepository.findByUsuarioId(currentUser.getId()).stream()
                    .map(Cliente::getId)
                    .toList();
            mascotas = clienteIds.isEmpty() ? List.of() : mascotaRepository.findByClienteIdIn(clienteIds);
        } else if (currentUser != null && currentUser.getEmpresa() != null) {
            List<String> clienteIds = clienteRepository.findByEmpresaId(currentUser.getEmpresa().getId()).stream()
                    .map(Cliente::getId)
                    .toList();
            mascotas = clienteIds.isEmpty() ? List.of() : mascotaRepository.findByClienteIdIn(clienteIds);
        } else {
            mascotas = mascotaRepository.findAll();
        }
        return mascotas.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<MascotaResponseDto> obtenerPorId(String id) {
        return mascotaRepository.findById(id)
                .map(this::mapToResponse);
    }

    @Transactional
    public MascotaResponseDto guardar(MascotaRequestDto dto) throws Exception {
        String nombre = ValidationUtils.requireText(dto.getNombre(), 120, "nombre");
        if (nombre == null || nombre.isBlank()) {
            log.error("Error al guardar mascota. El campo nombre es requerido");
            logService.error("Error al guardar mascota. El campo nombre es requerido");
            throw new OperationException("El campo nombre es requerido");
        }
        if (dto.getClienteId() == null || dto.getClienteId().isBlank()) {
            log.error("Error al guardar mascota. El campo cliente_id es requerido");
            logService.error("Error al guardar mascota. El campo cliente_id es requerido");
            throw new OperationException("El campo cliente_id es requerido");
        }
        if (dto.getRazaId() == null || dto.getRazaId().isBlank()) {
            log.error("Error al guardar mascota. El campo raza_id es requerido");
            logService.error("Error al guardar mascota. El campo raza_id es requerido");
            throw new OperationException("El campo raza_id es requerido");
        }

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new OperationException("Cliente no encontrado con id: " + dto.getClienteId()));

        Raza raza = razaRepository.findById(dto.getRazaId())
                .orElseThrow(() -> new OperationException("Raza no encontrada con id: " + dto.getRazaId()));

        Mascota mascota = new Mascota();
        mascota.setNombre(nombre);
        mascota.setCliente(cliente);
        mascota.setRaza(raza);
        if (dto.getActivo() != null) {
            mascota.setActivo(dto.getActivo());
        }

        logService.info("Mascota guardada exitosamente: " + nombre);
        return mapToResponse(mascotaRepository.save(mascota));
    }

    @Transactional
    public MascotaResponseDto update(String mascotaId, MascotaRequestDto dto) throws Exception {
        Mascota mascota = mascotaRepository.findById(mascotaId)
                .orElseThrow(() -> new OperationException("Mascota no encontrada con id: " + mascotaId));

        String nombre = ValidationUtils.requireText(dto.getNombre(), 120, "nombre");
        if (nombre == null || nombre.isBlank()) {
            log.error("Error al actualizar mascota. El campo nombre es requerido");
            logService.error("Error al actualizar mascota. El campo nombre es requerido");
            throw new OperationException("El campo nombre es requerido");
        }

        mascota.setNombre(nombre);

        if (dto.getClienteId() != null && !dto.getClienteId().isBlank()) {
            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new OperationException("Cliente no encontrado con id: " + dto.getClienteId()));
            mascota.setCliente(cliente);
        }

        if (dto.getRazaId() != null && !dto.getRazaId().isBlank()) {
            Raza raza = razaRepository.findById(dto.getRazaId())
                    .orElseThrow(() -> new OperationException("Raza no encontrada con id: " + dto.getRazaId()));
            mascota.setRaza(raza);
        }

        if (dto.getActivo() != null) {
            mascota.setActivo(dto.getActivo());
        }

        logService.info("Mascota actualizada exitosamente: " + mascotaId);
        return mapToResponse(mascotaRepository.save(mascota));
    }

    @Transactional
    public void eliminar(String id) throws Exception {
        if (!mascotaRepository.existsById(id)) {
            log.error("Error al eliminar mascota. No encontrada con id: {}", id);
            logService.error("Error al eliminar mascota. No encontrada con id: " + id);
            throw new OperationException("Mascota no encontrada con id: " + id);
        }
        mascotaRepository.deleteById(id);
        logService.info("Mascota eliminada exitosamente: " + id);
    }

    private MascotaResponseDto mapToResponse(Mascota mascota) {
        return MascotaResponseDto.builder()
                .id(mascota.getId())
                .nombre(mascota.getNombre())
                .clienteId(mascota.getCliente() != null ? mascota.getCliente().getId() : null)
                .razaId(mascota.getRaza() != null ? mascota.getRaza().getId() : null)
                .activo(mascota.isActivo())
                .build();
    }

    private Usuario getCurrentUser() {
        if (org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() != null &&
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Usuario) {
            return (Usuario) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return null;
    }
}
