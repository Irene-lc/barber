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
    private final EmpresaRepository repository;

    @Transactional
    public void save(EmpresaRequestDto empresa) throws Exception {

        if(StringUtil.isNullOrEmpty(empresa.getNombre())) {
            log.error("Error al guardar empresa. El campo nombre null");
            throw new Exception("El campo nombre es null");
        }

        if(StringUtil.isNullOrEmpty(empresa.getNit())) {
            log.error("Error al guardar empresa. El campo nit null");
            throw new Exception("El campo nit es null");
        }

        if(StringUtil.isNullOrEmpty(empresa.getRazon_social())) {
            log.error("Error al guardar empresa. El campo razon social null");
            throw new Exception("El campo razon social es null");
        }

        Empresa empresa1 = new Empresa();
        empresa1.setNombre(empresa.getNombre());
        empresa1.setActiva(empresa.isActiva());
        empresa1.setNit(empresa.getNit());
        empresa1.setRazonSocial(empresa.getRazon_social());

        this.repository.save(empresa1);
    }

    @Transactional(readOnly = true)
    public List<EmpresaResponseDto> listar() {
        return this.repository.findByNombreAux("Empresa 1");
    }

    @Transactional(readOnly = true)
    public Optional<Empresa> findByID(String id) {
        return this.repository.findById(id);
    }



}

