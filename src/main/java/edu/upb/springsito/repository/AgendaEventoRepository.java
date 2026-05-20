package edu.upb.springsito.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.upb.springsito.dto.AgendaEventoDto;
import edu.upb.springsito.repository.entity.AgendaEvento;

@Repository
public interface AgendaEventoRepository extends JpaRepository<AgendaEvento, String> {

    @Query("""
            SELECT new edu.upb.springsito.dto.AgendaEventoDto(
                c.nombre,
                ae.tipoEvento,
                ae.estado
            )
            FROM AgendaEvento ae
            LEFT JOIN ae.cliente c
            ORDER BY ae.inicio DESC
            """)
    List<AgendaEventoDto> listarDto();
}
