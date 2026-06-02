package edu.upb.barber.service;

import edu.upb.barber.repository.ComboServicioDetalleRepository;
import edu.upb.barber.repository.ComboServicioRepository;
import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.ServicioRepository;
import edu.upb.barber.repository.dto.request.ComboServicioDetalleRequestDto;
import edu.upb.barber.repository.dto.request.ComboServicioRequestDto;
import edu.upb.barber.repository.dto.response.ComboServicioDetalleResponseDto;
import edu.upb.barber.repository.dto.response.ComboServicioResponseDto;
import edu.upb.barber.repository.entity.ComboServicio;
import edu.upb.barber.repository.entity.ComboServicioDetalle;
import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.Servicio;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class ComboServicioService {

    private final ComboServicioRepository comboServicioRepository;
    private final ComboServicioDetalleRepository comboServicioDetalleRepository;
    private final EmpresaRepository empresaRepository;
    private final ServicioRepository servicioRepository;

    @Transactional(readOnly = true)
    public List<ComboServicioResponseDto> listar() {
        return comboServicioRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ComboServicioResponseDto> findById(String id) {
        return comboServicioRepository.findById(id)
                .map(this::mapToResponse);
    }

    @Transactional
    public ComboServicioResponseDto save(ComboServicioRequestDto dto) throws Exception {

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }
        if (dto.getPrecio() == null || dto.getPrecio().doubleValue() < 0) {
            throw new Exception("El precio debe ser mayor o igual a cero");
        }
        if (dto.getDuracionMinutos() == null || dto.getDuracionMinutos() <= 0) {
            throw new Exception("La duracion debe ser mayor a cero");
        }
        if (dto.getEmpresaId() == null || dto.getEmpresaId().isBlank()) {
            throw new Exception("El campo empresa_id es requerido");
        }

        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new Exception("Empresa no encontrada con id: " + dto.getEmpresaId()));

        ComboServicio combo = new ComboServicio();
        combo.setEmpresa(empresa);
        combo.setNombre(dto.getNombre());
        combo.setDescripcion(dto.getDescripcion());
        combo.setPrecio(dto.getPrecio());
        combo.setDuracionMinutos(dto.getDuracionMinutos());

        if (dto.getActivo() != null) {
            combo.setActivo(dto.getActivo());
        }

        combo = comboServicioRepository.save(combo);

        if (dto.getDetalles() != null) {
            for (ComboServicioDetalleRequestDto detDto : dto.getDetalles()) {
                Servicio servicio = servicioRepository.findById(detDto.getServicioId())
                        .orElseThrow(() -> new Exception("Servicio no encontrado con id: " + detDto.getServicioId()));

                ComboServicioDetalle detalle = new ComboServicioDetalle();
                detalle.setComboServicio(combo);
                detalle.setServicio(servicio);
                detalle.setOrdenEjecucion(detDto.getOrdenEjecucion());
                comboServicioDetalleRepository.save(detalle);
            }
        }

        return mapToResponse(combo);
    }

    @Transactional
    public ComboServicioResponseDto update(String comboId, ComboServicioRequestDto dto) throws Exception {

        ComboServicio combo = comboServicioRepository.findById(comboId)
                .orElseThrow(() -> new Exception("ComboServicio no encontrado con id: " + comboId));

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }
        if (dto.getPrecio() == null || dto.getPrecio().doubleValue() < 0) {
            throw new Exception("El precio debe ser mayor o igual a cero");
        }
        if (dto.getDuracionMinutos() == null || dto.getDuracionMinutos() <= 0) {
            throw new Exception("La duracion debe ser mayor a cero");
        }

        combo.setNombre(dto.getNombre());
        combo.setDescripcion(dto.getDescripcion());
        combo.setPrecio(dto.getPrecio());
        combo.setDuracionMinutos(dto.getDuracionMinutos());

        if (dto.getActivo() != null) {
            combo.setActivo(dto.getActivo());
        }

        combo = comboServicioRepository.save(combo);

        if (dto.getDetalles() != null) {
            // Eliminar detalles previos
            comboServicioDetalleRepository.deleteByComboServicioId(comboId);

            for (ComboServicioDetalleRequestDto detDto : dto.getDetalles()) {
                Servicio servicio = servicioRepository.findById(detDto.getServicioId())
                        .orElseThrow(() -> new Exception("Servicio no encontrado con id: " + detDto.getServicioId()));

                ComboServicioDetalle detalle = new ComboServicioDetalle();
                detalle.setComboServicio(combo);
                detalle.setServicio(servicio);
                detalle.setOrdenEjecucion(detDto.getOrdenEjecucion());
                comboServicioDetalleRepository.save(detalle);
            }
        }

        return mapToResponse(combo);
    }

    @Transactional
    public void delete(String id) throws Exception {
        if (!comboServicioRepository.existsById(id)) {
            throw new Exception("ComboServicio no encontrado con id: " + id);
        }
        comboServicioDetalleRepository.deleteByComboServicioId(id);
        comboServicioRepository.deleteById(id);
    }

    private ComboServicioResponseDto mapToResponse(ComboServicio combo) {
        ComboServicioResponseDto res = new ComboServicioResponseDto(combo);
        List<ComboServicioDetalle> detalles = comboServicioDetalleRepository.findByComboServicioId(combo.getId());
        res.setDetalles(detalles.stream()
                .map(ComboServicioDetalleResponseDto::new)
                .collect(Collectors.toList()));
        return res;
    }
}