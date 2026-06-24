package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Venta;
import edu.upb.barber.repository.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, String> {
    List<Venta> findByClienteIdIn(List<String> clienteIds);
    List<Venta> findBySucursal_Empresa(Empresa empresa);
}
