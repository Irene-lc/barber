package edu.upb.barber.service;

import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.SucursalRepository;
import edu.upb.barber.repository.dto.request.SucursalRequestDto;
import edu.upb.barber.repository.dto.response.SucursalResponseDto;
import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.Sucursal;
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

    @Transactional(readOnly = true)
    public List<SucursalResponseDto> listar() {
        Usuario currentUser = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null && 
            SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Usuario) {
            currentUser = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }

        String empresaId = (currentUser != null && currentUser.getEmpresa() != null) 
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
            throw new Exception("El campo nombre es requerido");
        }
        if (dto.getEmpresaId() == null || dto.getEmpresaId().isBlank()) {
            throw new Exception("El campo empresa_id es requerido");
        }

        Usuario currentUser = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null && 
            SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Usuario) {
            currentUser = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }

        if (currentUser != null && currentUser.getEmpresa() != null) {
            if (!currentUser.getEmpresa().getId().equals(dto.getEmpresaId())) {
                throw new Exception("No tienes permiso para operar en la empresa especificada");
            }
        }

        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new Exception("Empresa no encontrada con id: " + dto.getEmpresaId()));

        Sucursal sucursal = new Sucursal();
        sucursal.setNombre(dto.getNombre());
        sucursal.setDireccion(dto.getDireccion());
        sucursal.setTelefono(dto.getTelefono());
        sucursal.setEmpresa(empresa);
        if (dto.getActivo() != null) {
            sucursal.setActivo(dto.getActivo());
        }

        return mapToResponse(sucursalRepository.save(sucursal));
    }

    @Transactional
    public SucursalResponseDto update(String sucursalId, SucursalRequestDto dto) throws Exception {
        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new Exception("Sucursal no encontrada con id: " + sucursalId));

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }

        sucursal.setNombre(dto.getNombre());
        sucursal.setDireccion(dto.getDireccion());
        sucursal.setTelefono(dto.getTelefono());

        if (dto.getEmpresaId() != null && !dto.getEmpresaId().isBlank()) {
            Usuario currentUser = null;
            if (SecurityContextHolder.getContext().getAuthentication() != null && 
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Usuario) {
                currentUser = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            }

            if (currentUser != null && currentUser.getEmpresa() != null) {
                if (!currentUser.getEmpresa().getId().equals(dto.getEmpresaId())) {
                    throw new Exception("No tienes permiso para operar en la empresa especificada");
                }
            }

            Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new Exception("Empresa no encontrada con id: " + dto.getEmpresaId()));
            sucursal.setEmpresa(empresa);
        }

        if (dto.getActivo() != null) {
            sucursal.setActivo(dto.getActivo());
        }

        return mapToResponse(sucursalRepository.save(sucursal));
    }

    @Transactional
    public void delete(String id) throws Exception {
        if (!sucursalRepository.existsById(id)) {
            throw new Exception("Sucursal no encontrada con id: " + id);
        }
        sucursalRepository.deleteById(id);
    }

    private SucursalResponseDto mapToResponse(Sucursal sucursal) {
        return SucursalResponseDto.builder()
                .id(sucursal.getId())
                .nombre(sucursal.getNombre())
                .direccion(sucursal.getDireccion())
                .telefono(sucursal.getTelefono())
                .empresaId(sucursal.getEmpresa() != null ? sucursal.getEmpresa().getId() : null)
                .empresaNombre(sucursal.getEmpresa() != null ? sucursal.getEmpresa().getNombre() : null)
                .tipoEmpresa(sucursal.getEmpresa() != null ? sucursal.getEmpresa().getTipoEmpresa() : null)
                .activo(sucursal.isActivo())
                .build();
    }
}
