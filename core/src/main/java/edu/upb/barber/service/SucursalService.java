package edu.upb.barber.service;

import edu.upb.barber.repository.SucursalRepository;
import edu.upb.barber.repository.entity.Sucursal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class SucursalService {

    private final SucursalRepository sucursalRepository;

    @Transactional(readOnly = true)
    public List<Sucursal> listar() {
        return sucursalRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Sucursal> findById(String id) {
        return sucursalRepository.findById(id);
    }

    @Transactional
    public void save(Sucursal sucursal) {
        sucursalRepository.save(sucursal);
    }

    @Transactional
    public void delete(String id) {
        sucursalRepository.deleteById(id);
    }
}
