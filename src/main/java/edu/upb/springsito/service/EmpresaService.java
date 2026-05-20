package edu.upb.springsito.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.upb.springsito.dto.EmpresaGuardarDto;
import edu.upb.springsito.dto.EmpresaListarDto;
import edu.upb.springsito.repository.EmpresaRepository;
import edu.upb.springsito.repository.entity.Empresa;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class EmpresaService {

    private final EmpresaRepository repository;

    @Transactional(propagation = Propagation.REQUIRED)
    public EmpresaListarDto save(EmpresaGuardarDto dto) {
        Empresa entity = new Empresa();
        entity.setNombre(dto.nombre());
        entity.setRazonSocial(dto.razonSocial());
        entity.setNit(dto.nit());
        entity.setTelefono(dto.telefono());
        entity.setEmail(dto.email());
        if (dto.activa() != null) {
            entity.setActiva(dto.activa());
        }

        return toListarDto(repository.save(entity));
    }

    public List<EmpresaListarDto> listar() {
        return repository.listar()
                .stream()
                .map(this::toListarDto)
                .collect(Collectors.toList());
    }

    
    public Empresa buscarPorNombreEmpresa(String nombre) {
        return repository.buscarPorNombreEmpresa(nombre);
    }

    private EmpresaListarDto toListarDto(Empresa empresa) {
        return new EmpresaListarDto(
            
                empresa.getNombre(),
                empresa.getRazonSocial(),
                empresa.getNit(),
                empresa.getTelefono(),
                empresa.getEmail(),
                empresa.isActiva());
    }
}
