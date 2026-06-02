package edu.upb.barber.service;

import ch.qos.logback.core.util.StringUtil;
import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.dto.request.EmpresaRequestDto;
import edu.upb.barber.repository.dto.response.EmpresaResponseDto;
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
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    @Transactional
    public void save(EmpresaRequestDto empresaRequestDto)
            throws Exception {

        if (StringUtil.isNullOrEmpty(
                empresaRequestDto.getNombre())) {

            log.error("El campo nombre es null");

            throw new Exception(
                    "El campo nombre es null");
        }

        if (StringUtil.isNullOrEmpty(
                empresaRequestDto.getRazonSocial())) {

            log.error("El campo razon social es null");

            throw new Exception(
                    "El campo razon social es null");
        }

        if (StringUtil.isNullOrEmpty(
                empresaRequestDto.getNit())) {

            log.error("El campo nit es null");

            throw new Exception(
                    "El campo nit es null");
        }

        Optional<Empresa> empresaExistente =
                empresaRepository.findByNit(
                        empresaRequestDto.getNit());

        if (empresaExistente.isPresent()) {

            log.error("Ya existe una empresa con ese nit");

            throw new Exception(
                    "Ya existe una empresa con ese nit");
        }

        Empresa empresa = new Empresa();

        empresa.setNombre(
                empresaRequestDto.getNombre());

        empresa.setRazonSocial(
                empresaRequestDto.getRazonSocial());

        empresa.setNit(
                empresaRequestDto.getNit());

        empresa.setTelefono(
                empresaRequestDto.getTelefono());

        empresa.setEmail(
                empresaRequestDto.getEmail());

        if (empresaRequestDto.getActivo() != null) {
            empresa.setActivo(
                    empresaRequestDto.getActivo());
        }

        empresaRepository.save(empresa);
    }

    @Transactional(readOnly = true)
    public List<EmpresaResponseDto> listar() {

        return empresaRepository.findAll()
                .stream()
                .map(EmpresaResponseDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<Empresa> findById(String id) {

        return empresaRepository.findById(id);
    }

    @Transactional
    public void update(String empresaId, EmpresaRequestDto empresa) throws Exception {
        if(StringUtil.isNullOrEmpty(empresa.getNombre())) {
            log.error("Error al guardar empresa. El campo nombre null");
            throw new Exception("El campo nombre es null");
        }

        if(StringUtil.isNullOrEmpty(empresa.getNit())) {
            log.error("Error al guardar empresa. El campo Descripcion null");
            throw new Exception("El campo Descripcion es null");
        }

        Optional<Empresa> optionalEmpresa = this.empresaRepository.findById(empresaId);
        if (optionalEmpresa.isEmpty()) {
            throw new Exception("No existe ek enoresa conn el id: " + empresaId);
        }
        Empresa empresa1 = optionalEmpresa.get();

        empresa1.setNit(empresa.getNit());
        empresa1.setRazonSocial(empresa.getRazonSocial());
        empresa1.setNombre(empresa.getNombre());
        empresa1.setTelefono(empresa.getTelefono());
        empresa1.setEmail(empresa.getEmail());

        if (empresa.getActivo() != null) {
            empresa1.setActivo(empresa.getActivo());
        }

        this.empresaRepository.save(empresa1);
    }
    @Transactional
    public void delete(String empresaId) throws Exception {
        if (!empresaRepository.existsById(empresaId)) {
            throw new Exception("Empresa no encontrada con id: " + empresaId);
        }
        empresaRepository.deleteById(empresaId);
    }


}