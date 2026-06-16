package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.AgendaEvento;
import edu.upb.barber.repository.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendaEventoRepository extends JpaRepository<AgendaEvento, String> {
    List<AgendaEvento> findBySucursal_Empresa(Empresa empresa);
}
