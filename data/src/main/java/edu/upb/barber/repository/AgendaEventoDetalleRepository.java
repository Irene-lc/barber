package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.AgendaEventoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendaEventoDetalleRepository extends JpaRepository<AgendaEventoDetalle, String> {
    void deleteByAgendaEventoId(String agendaEventoId);

    List<AgendaEventoDetalle> findByAgendaEventoId(String agendaEventoId);
}
