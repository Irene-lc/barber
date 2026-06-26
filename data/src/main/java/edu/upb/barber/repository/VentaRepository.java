package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Venta;
import edu.upb.barber.repository.entity.Empresa;
import edu.upb.barber.repository.entity.enums.EstadoVenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, String> {
    List<Venta> findByClienteIdIn(List<String> clienteIds);
    List<Venta> findBySucursal_Empresa(Empresa empresa);
    Optional<Venta> findByAgendaEventoId(String agendaEventoId);
    List<Venta> findByEstadoAndCreatedDateBefore(EstadoVenta estado, LocalDateTime threshold);
}
