package edu.upb.barber.service;

import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.ServicioRepository;
import edu.upb.barber.repository.dto.request.ServicioRequestDto;
import edu.upb.barber.repository.dto.response.ServicioResponseDto;
import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.Servicio;
import edu.upb.barber.repository.entity.enums.CategoriaServicio;
import edu.upb.barber.repository.entity.enums.TipoDestinatarioServicio;
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

    @Transactional(readOnly = true)
    public List<ServicioResponseDto> listar() {
        Usuario currentUser = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null && 
            SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Usuario) {
            currentUser = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }

        String empresaId = (currentUser != null && currentUser.getEmpresa() != null) 
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
            throw new Exception("El campo nombre es requerido");
        }
        if (dto.getPrecio() == null || dto.getPrecio() < 0) {
            throw new Exception("El precio debe ser mayor o igual a cero");
        }
        if (dto.getDuracion() == null || dto.getDuracion() <= 0) {
            throw new Exception("La duracion debe ser mayor a cero");
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

        Servicio servicio = new Servicio();
        servicio.setNombre(dto.getNombre());
        servicio.setDescripcion(dto.getDescripcion());
        servicio.setPrecioBase(BigDecimal.valueOf(dto.getPrecio()));
        servicio.setDuracionMinutos(dto.getDuracion());
        servicio.setEmpresa(empresa);

        if (dto.getDestinatario() != null) {
            servicio.setDestinatario(TipoDestinatarioServicio.valueOf(dto.getDestinatario()));
        } else {
            servicio.setDestinatario(TipoDestinatarioServicio.HUMANO); // default
        }

        if (dto.getCategoria() != null) {
            servicio.setCategoria(CategoriaServicio.valueOf(dto.getCategoria()));
        }

        if (dto.getActivo() != null) {
            servicio.setActivo(dto.getActivo());
        }

        return new ServicioResponseDto(servicioRepository.save(servicio));
    }

    @Transactional
    public ServicioResponseDto update(String servicioId, ServicioRequestDto dto) throws Exception {
        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new Exception("Servicio no encontrado con id: " + servicioId));

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }
        if (dto.getPrecio() == null || dto.getPrecio() < 0) {
            throw new Exception("El precio debe ser mayor o igual a cero");
        }
        if (dto.getDuracion() == null || dto.getDuracion() <= 0) {
            throw new Exception("La duracion debe ser mayor a cero");
        }

        servicio.setNombre(dto.getNombre());
        servicio.setDescripcion(dto.getDescripcion());
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
                    throw new Exception("No tienes permiso para operar en la empresa especificada");
                }
            }

            Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new Exception("Empresa no encontrada con id: " + dto.getEmpresaId()));
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

        return new ServicioResponseDto(servicioRepository.save(servicio));
    }

    @Transactional
    public void delete(String id) throws Exception {
        if (!servicioRepository.existsById(id)) {
            throw new Exception("Servicio no encontrado con id: " + id);
        }
        servicioRepository.deleteById(id);
    }
}
