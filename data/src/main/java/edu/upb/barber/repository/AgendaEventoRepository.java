package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.AgendaEvento;
import edu.upb.barber.repository.entity.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendaEventoRepository extends JpaRepository<AgendaEvento, String> {
    List<AgendaEvento> findBySucursal_Empresa(Empresa empresa);
    Page<AgendaEvento> findBySucursal_Empresa(Empresa empresa, Pageable pageable);
    List<AgendaEvento> findBySucursalId(String sucursalId);
    Page<AgendaEvento> findBySucursalId(String sucursalId, Pageable pageable);
    List<AgendaEvento> findByClienteIdIn(List<String> clienteIds);
    Page<AgendaEvento> findByClienteIdIn(List<String> clienteIds, Pageable pageable);
}
