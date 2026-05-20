package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.AgendaEvento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendaEventoRepository extends JpaRepository<AgendaEvento, String> {
}
