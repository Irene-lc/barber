package edu.upb.barber.service;

import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.SucursalRepository;
import edu.upb.barber.repository.dto.request.SucursalRequestDto;
import edu.upb.barber.repository.dto.response.SucursalResponseDto;
import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.Sucursal;
import edu.upb.barber.service.exception.OperationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.upb.barber.repository.entity.Usuario;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@AllArgsConstructor
@Service
public class SucursalService {

    private final SucursalRepository sucursalRepository;
    private final EmpresaRepository empresaRepository;
    private final LogService logService;

    @Transactional(readOnly = true)
    public List<SucursalResponseDto> listar() {
        Usuario currentUser = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
            SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Usuario) {
            currentUser = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }

        String empresaId = (currentUser != null && currentUser.getEmpresa() != null && currentUser.getRol() != edu.upb.barber.repository.entity.enums.RolUsuario.ROLE_CLIENTE)
                ? currentUser.getEmpresa().getId()
                : null;

        List<Sucursal> sucursales = (empresaId != null)
                ? sucursalRepository.findByEmpresaId(empresaId)
                : sucursalRepository.findAll();

        return sucursales.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<SucursalResponseDto> findById(String id) {
        return sucursalRepository.findById(id)
                .map(this::mapToResponse);
    }

    @Transactional
    public SucursalResponseDto save(SucursalRequestDto dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            log.error("Error al guardar sucursal. El campo nombre es requerido");
            logService.error("Error al guardar sucursal. El campo nombre es requerido");
            throw new OperationException("El campo nombre es requerido");
        }
        if (dto.getEmpresaId() == null || dto.getEmpresaId().isBlank()) {
            log.error("Error al guardar sucursal. El campo empresa_id es requerido");
            logService.error("Error al guardar sucursal. El campo empresa_id es requerido");
            throw new OperationException("El campo empresa_id es requerido");
        }

        Usuario currentUser = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
            SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Usuario) {
            currentUser = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }

        if (currentUser != null && currentUser.getEmpresa() != null) {
            if (!currentUser.getEmpresa().getId().equals(dto.getEmpresaId())) {
                log.error("Error al guardar sucursal. Sin permiso para operar en empresa: {}", dto.getEmpresaId());
                logService.error("Error al guardar sucursal. Sin permiso para operar en empresa: " + dto.getEmpresaId());
                throw new OperationException("No tienes permiso para operar en la empresa especificada");
            }
        }

        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new OperationException("Empresa no encontrada con id: " + dto.getEmpresaId()));

        Sucursal sucursal = new Sucursal();
        sucursal.setNombre(dto.getNombre());
        sucursal.setDireccion(dto.getDireccion());
        sucursal.setTelefono(dto.getTelefono());
        sucursal.setImagenUrl(dto.getImagenUrl() != null && !dto.getImagenUrl().isBlank() ? dto.getImagenUrl() : null);
        sucursal.setEmpresa(empresa);
        if (dto.getActivo() != null) {
            sucursal.setActivo(dto.getActivo());
        }

        logService.info("Sucursal guardada exitosamente: " + dto.getNombre());
        return mapToResponse(sucursalRepository.save(sucursal));
    }

    @Transactional
    public SucursalResponseDto update(String sucursalId, SucursalRequestDto dto) throws Exception {
        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new OperationException("Sucursal no encontrada con id: " + sucursalId));

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            log.error("Error al actualizar sucursal. El campo nombre es requerido");
            logService.error("Error al actualizar sucursal. El campo nombre es requerido");
            throw new OperationException("El campo nombre es requerido");
        }

        sucursal.setNombre(dto.getNombre());
        sucursal.setDireccion(dto.getDireccion());
        sucursal.setTelefono(dto.getTelefono());
        if (dto.getImagenUrl() != null) {
            sucursal.setImagenUrl(dto.getImagenUrl().isBlank() ? null : dto.getImagenUrl());
        }

        if (dto.getEmpresaId() != null && !dto.getEmpresaId().isBlank()) {
            Usuario currentUser = null;
            if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Usuario) {
                currentUser = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            }

            if (currentUser != null && currentUser.getEmpresa() != null) {
                if (!currentUser.getEmpresa().getId().equals(dto.getEmpresaId())) {
                    log.error("Error al actualizar sucursal. Sin permiso para operar en empresa: {}", dto.getEmpresaId());
                    logService.error("Error al actualizar sucursal. Sin permiso para operar en empresa: " + dto.getEmpresaId());
                    throw new OperationException("No tienes permiso para operar en la empresa especificada");
                }
            }

            Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new OperationException("Empresa no encontrada con id: " + dto.getEmpresaId()));
            sucursal.setEmpresa(empresa);
        }

        if (dto.getActivo() != null) {
            sucursal.setActivo(dto.getActivo());
        }

        logService.info("Sucursal actualizada exitosamente: " + sucursalId);
        return mapToResponse(sucursalRepository.save(sucursal));
    }

    @Transactional
    public void delete(String id) throws Exception {
        if (!sucursalRepository.existsById(id)) {
            log.error("Error al eliminar sucursal. No encontrada con id: {}", id);
            logService.error("Error al eliminar sucursal. No encontrada con id: " + id);
            throw new OperationException("Sucursal no encontrada con id: " + id);
        }
        sucursalRepository.deleteById(id);
        logService.info("Sucursal eliminada exitosamente: " + id);
    }

    private SucursalResponseDto mapToResponse(Sucursal sucursal) {
        return SucursalResponseDto.builder()
                .id(sucursal.getId())
                .nombre(sucursal.getNombre())
                .direccion(sucursal.getDireccion())
                .telefono(sucursal.getTelefono())
                .imagenUrl(sucursal.getImagenUrl())
                .empresaId(sucursal.getEmpresa() != null ? sucursal.getEmpresa().getId() : null)
                .empresaNombre(sucursal.getEmpresa() != null ? sucursal.getEmpresa().getNombre() : null)
                .tipoEmpresa(sucursal.getEmpresa() != null ? sucursal.getEmpresa().getTipoEmpresa() : null)
                .activo(sucursal.isActivo())
                .build();
    }
}
