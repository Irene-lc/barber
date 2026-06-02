package edu.upb.barber.service;

import edu.upb.barber.repository.ComboServicioRepository;
import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.dto.request.ComboServicioRequestDto;
import edu.upb.barber.repository.entity.ComboServicio;
import edu.upb.barber.repository.entity.Empresa;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class ComboServicioService {

    private final ComboServicioRepository comboServicioRepository;
    private final EmpresaRepository empresaRepository;

    @Transactional(readOnly = true)
    public List<ComboServicio> listar() {
        return comboServicioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<ComboServicio> findById(String id) {
        return comboServicioRepository.findById(id);
    }

    @Transactional
    public ComboServicio save(ComboServicioRequestDto dto) throws Exception {

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }
        if (dto.getPrecio() == null || dto.getPrecio().doubleValue() < 0) {
            throw new Exception("El precio debe ser mayor o igual a cero");
        }
        if (dto.getDuracionMinutos() == null || dto.getDuracionMinutos() <= 0) {
            throw new Exception("La duracion debe ser mayor a cero");
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

        return comboServicioRepository.save(combo);
    }

    @Transactional
    public ComboServicio update(String comboId, ComboServicioRequestDto dto) throws Exception {

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

        return comboServicioRepository.save(combo);
    }

    @Transactional
    public void delete(String id) throws Exception {
        if (!comboServicioRepository.existsById(id)) {
            throw new Exception("ComboServicio no encontrado con id: " + id);
        }
        comboServicioRepository.deleteById(id);
    }
}