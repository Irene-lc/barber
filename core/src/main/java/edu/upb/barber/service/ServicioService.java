package edu.upb.barber.service;

import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.ServicioRepository;
import edu.upb.barber.repository.dto.request.ServicioRequestDto;
import edu.upb.barber.repository.dto.response.ServicioResponseDto;
import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.Servicio;
import edu.upb.barber.repository.entity.enums.CategoriaServicio;
import edu.upb.barber.repository.entity.enums.TipoDestinatarioServicio;
import edu.upb.barber.service.exception.OperationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.upb.barber.repository.entity.Usuario;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@AllArgsConstructor
@Service
public class ServicioService {

    private final ServicioRepository servicioRepository;
    private final EmpresaRepository empresaRepository;
    private final LogService logService;

    @Transactional(readOnly = true)
    public List<ServicioResponseDto> listar() {
        Usuario currentUser = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
            SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Usuario) {
            currentUser = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }

        String empresaId = (currentUser != null && currentUser.getEmpresa() != null && currentUser.getRol() != edu.upb.barber.repository.entity.enums.RolUsuario.ROLE_CLIENTE)
                ? currentUser.getEmpresa().getId()
                : null;

        List<Servicio> servicios = (empresaId != null)
                ? servicioRepository.findByEmpresaId(empresaId)
                : servicioRepository.findAll();

        return servicios.stream()
                .map(ServicioResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ServicioResponseDto> findById(String id) {
        return servicioRepository.findById(id)
                .map(ServicioResponseDto::new);
    }

    @Transactional
    public ServicioResponseDto save(ServicioRequestDto dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            log.error("Error al guardar servicio. El campo nombre es requerido");
            logService.error("Error al guardar servicio. El campo nombre es requerido");
            throw new OperationException("El campo nombre es requerido");
        }
        if (dto.getPrecio() == null || dto.getPrecio() < 0) {
            log.error("Error al guardar servicio. El precio debe ser mayor o igual a cero");
            logService.error("Error al guardar servicio. El precio debe ser mayor o igual a cero");
            throw new OperationException("El precio debe ser mayor o igual a cero");
        }
        if (dto.getDuracion() == null || dto.getDuracion() <= 0) {
            log.error("Error al guardar servicio. La duracion debe ser mayor a cero");
            logService.error("Error al guardar servicio. La duracion debe ser mayor a cero");
            throw new OperationException("La duracion debe ser mayor a cero");
        }
        if (dto.getEmpresaId() == null || dto.getEmpresaId().isBlank()) {
            log.error("Error al guardar servicio. El campo empresa_id es requerido");
            logService.error("Error al guardar servicio. El campo empresa_id es requerido");
            throw new OperationException("El campo empresa_id es requerido");
        }

        Usuario currentUser = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
            SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Usuario) {
            currentUser = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }

        if (currentUser != null && currentUser.getEmpresa() != null) {
            if (!currentUser.getEmpresa().getId().equals(dto.getEmpresaId())) {
                log.error("Error al guardar servicio. Sin permiso para operar en empresa: {}", dto.getEmpresaId());
                logService.error("Error al guardar servicio. Sin permiso para operar en empresa: " + dto.getEmpresaId());
                throw new OperationException("No tienes permiso para operar en la empresa especificada");
            }
        }

        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new OperationException("Empresa no encontrada con id: " + dto.getEmpresaId()));

        Servicio servicio = new Servicio();
        servicio.setNombre(dto.getNombre());
        servicio.setDescripcion(dto.getDescripcion());
        servicio.setImagenUrl(dto.getImagenUrl() != null && !dto.getImagenUrl().isBlank() ? dto.getImagenUrl() : null);
        servicio.setPrecioBase(BigDecimal.valueOf(dto.getPrecio()));
        servicio.setDuracionMinutos(dto.getDuracion());
        servicio.setEmpresa(empresa);

        if (dto.getDestinatario() != null) {
            servicio.setDestinatario(TipoDestinatarioServicio.valueOf(dto.getDestinatario()));
        } else {
            servicio.setDestinatario(TipoDestinatarioServicio.HUMANO);
        }

        if (dto.getCategoria() != null) {
            servicio.setCategoria(CategoriaServicio.valueOf(dto.getCategoria()));
        }

        if (dto.getActivo() != null) {
            servicio.setActivo(dto.getActivo());
        }

        logService.info("Servicio guardado exitosamente: " + dto.getNombre());
        return new ServicioResponseDto(servicioRepository.save(servicio));
    }

    @Transactional
    public ServicioResponseDto update(String servicioId, ServicioRequestDto dto) throws Exception {
        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new OperationException("Servicio no encontrado con id: " + servicioId));

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            log.error("Error al actualizar servicio. El campo nombre es requerido");
            logService.error("Error al actualizar servicio. El campo nombre es requerido");
            throw new OperationException("El campo nombre es requerido");
        }
        if (dto.getPrecio() == null || dto.getPrecio() < 0) {
            log.error("Error al actualizar servicio. El precio debe ser mayor o igual a cero");
            logService.error("Error al actualizar servicio. El precio debe ser mayor o igual a cero");
            throw new OperationException("El precio debe ser mayor o igual a cero");
        }
        if (dto.getDuracion() == null || dto.getDuracion() <= 0) {
            log.error("Error al actualizar servicio. La duracion debe ser mayor a cero");
            logService.error("Error al actualizar servicio. La duracion debe ser mayor a cero");
            throw new OperationException("La duracion debe ser mayor a cero");
        }

        servicio.setNombre(dto.getNombre());
        servicio.setDescripcion(dto.getDescripcion());
        if (dto.getImagenUrl() != null) {
            servicio.setImagenUrl(dto.getImagenUrl().isBlank() ? null : dto.getImagenUrl());
        }
        servicio.setPrecioBase(BigDecimal.valueOf(dto.getPrecio()));
        servicio.setDuracionMinutos(dto.getDuracion());

        if (dto.getEmpresaId() != null && !dto.getEmpresaId().isBlank()) {
            Usuario currentUser = null;
            if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Usuario) {
                currentUser = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            }

            if (currentUser != null && currentUser.getEmpresa() != null) {
                if (!currentUser.getEmpresa().getId().equals(dto.getEmpresaId())) {
                    log.error("Error al actualizar servicio. Sin permiso para operar en empresa: {}", dto.getEmpresaId());
                    logService.error("Error al actualizar servicio. Sin permiso para operar en empresa: " + dto.getEmpresaId());
                    throw new OperationException("No tienes permiso para operar en la empresa especificada");
                }
            }

            Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new OperationException("Empresa no encontrada con id: " + dto.getEmpresaId()));
            servicio.setEmpresa(empresa);
        }

        if (dto.getDestinatario() != null) {
            servicio.setDestinatario(TipoDestinatarioServicio.valueOf(dto.getDestinatario()));
        }

        if (dto.getCategoria() != null) {
            servicio.setCategoria(CategoriaServicio.valueOf(dto.getCategoria()));
        } else {
            servicio.setCategoria(null);
        }

        if (dto.getActivo() != null) {
            servicio.setActivo(dto.getActivo());
        }

        logService.info("Servicio actualizado exitosamente: " + servicioId);
        return new ServicioResponseDto(servicioRepository.save(servicio));
    }

    @Transactional
    public void delete(String id) throws Exception {
        if (!servicioRepository.existsById(id)) {
            log.error("Error al eliminar servicio. No encontrado con id: {}", id);
            logService.error("Error al eliminar servicio. No encontrado con id: " + id);
            throw new OperationException("Servicio no encontrado con id: " + id);
        }
        servicioRepository.deleteById(id);
        logService.info("Servicio eliminado exitosamente: " + id);
    }
}
