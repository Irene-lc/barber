package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.AgendaEventoEmpleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

public interface AgendaEventoEmpleadoRepository extends JpaRepository<AgendaEventoEmpleado, String> {

    @Query("""
            SELECT CASE WHEN COUNT(aee) > 0 THEN true ELSE false END
            FROM AgendaEventoEmpleado aee
            WHERE aee.empleado.id = :empleadoId
              AND aee.agendaEvento.inicio < :nuevoFin
              AND aee.agendaEvento.fin > :nuevoInicio
              AND aee.agendaEvento.estado NOT IN :estadosIgnorados
            """)
    boolean existsConflictoHorarioEmpleado(
            @Param("empleadoId") String empleadoId,
            @Param("nuevoInicio") OffsetDateTime nuevoInicio,
            @Param("nuevoFin") OffsetDateTime nuevoFin,
            @Param("estadosIgnorados") Collection<?> estadosIgnorados
    );

    @Query("""
            SELECT CASE WHEN COUNT(aee) > 0 THEN true ELSE false END
            FROM AgendaEventoEmpleado aee
            WHERE aee.empleado.id = :empleadoId
              AND aee.agendaEvento.id <> :excludeEventoId
              AND aee.agendaEvento.inicio < :nuevoFin
              AND aee.agendaEvento.fin > :nuevoInicio
              AND aee.agendaEvento.estado NOT IN :estadosIgnorados
            """)
    boolean existsConflictoHorarioEmpleadoExcludingEvent(
            @Param("empleadoId") String empleadoId,
            @Param("excludeEventoId") String excludeEventoId,
            @Param("nuevoInicio") OffsetDateTime nuevoInicio,
            @Param("nuevoFin") OffsetDateTime nuevoFin,
            @Param("estadosIgnorados") Collection<?> estadosIgnorados
    );

    void deleteByAgendaEventoId(String agendaEventoId);

    List<AgendaEventoEmpleado> findByAgendaEventoId(String agendaEventoId);
}
