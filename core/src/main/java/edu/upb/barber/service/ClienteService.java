package edu.upb.barber.service;

import edu.upb.barber.repository.ClienteRepository;
import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.dto.request.ClienteRequestDto;
import edu.upb.barber.repository.dto.response.ClienteResponseDto;
import edu.upb.barber.repository.entity.Cliente;
import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.Log;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EmpresaRepository empresaRepository;

    @Transactional(readOnly = true)
    public List<ClienteResponseDto> listar() {
        return clienteRepository.findAll().stream()
                .map(ClienteResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ClienteResponseDto> findById(String id) {
        return clienteRepository.findById(id)
                .map(ClienteResponseDto::new);
    }

    @Transactional
    public ClienteResponseDto save(ClienteRequestDto dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }
        if (dto.getEmpresaId() == null || dto.getEmpresaId().isBlank()) {
            throw new Exception("El campo empresa_id es requerido");
        }

        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new Exception("Empresa no encontrada con id: " + dto.getEmpresaId()));

        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());
        cliente.setDocumento(dto.getDocumento());
        cliente.setNotas(dto.getNotas());
        cliente.setEmpresa(empresa);
        cliente.setActivo(dto.isActivo());

        return new ClienteResponseDto(clienteRepository.save(cliente));
    }

    @Transactional
    public ClienteResponseDto update(String clienteId, ClienteRequestDto dto) throws Exception {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new Exception("Cliente no encontrado con id: " + clienteId));

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new Exception("El campo nombre es requerido");
        }

        cliente.setNombre(dto.getNombre());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());
        cliente.setDocumento(dto.getDocumento());
        cliente.setNotas(dto.getNotas());
        cliente.setActivo(dto.isActivo());

        if (dto.getEmpresaId() != null && !dto.getEmpresaId().isBlank()) {
            Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new Exception("Empresa no encontrada con id: " + dto.getEmpresaId()));
            cliente.setEmpresa(empresa);
        }

        return new ClienteResponseDto(clienteRepository.save(cliente));
    }

    @Transactional
    public void delete(String id) throws Exception {
        if (!clienteRepository.existsById(id)) {
            throw new Exception("Cliente no encontrado con id: " + id);
        }
        clienteRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<ClienteResponseDto> findAllByOderByDataDesc(
            LocalDateTime pInit,
            LocalDateTime pEnd,
            Pageable page) {

        return clienteRepository
                .findAllByOderByDataDesc(pInit, pEnd, page)
                .map(ClienteResponseDto::new);
    }
}
