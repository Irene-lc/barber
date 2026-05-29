package edu.upb.barber.service;

import edu.upb.barber.repository.ClienteRepository;
import edu.upb.barber.repository.EmpresaRepository;
import edu.upb.barber.repository.dto.request.ClienteRequestDto;
import edu.upb.barber.repository.entity.Cliente;
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
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EmpresaRepository empresaRepository;

    @Transactional(readOnly = true)
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> findById(String id) {
        return clienteRepository.findById(id);
    }

    @Transactional
    public void save(Cliente cliente) {
        clienteRepository.save(cliente);
    }

    @Transactional
    public void delete(String id) {
        clienteRepository.deleteById(id);
    }

    @Transactional
    public void save(ClienteRequestDto dto) throws Exception {

        Empresa empresa = empresaRepository.findById(dto.getEmpresa())
                .orElseThrow(() -> new Exception("Empresa no encontrada con id: " + dto.getEmpresa()));

        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getCorreo());
        cliente.setEmpresa(empresa);  // objeto completo, no solo el id

        clienteRepository.save(cliente);
    }
}
