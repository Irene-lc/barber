package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Cliente;
import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MascotaRepository extends JpaRepository<Mascota, String> {
    Optional<Mascota> findByClienteAndNombre(Cliente cliente, String nombre);

    List<Mascota> findByClienteIdIn(List<String> clienteIds);

}
