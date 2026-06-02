package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.ComboServicioDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComboServicioDetalleRepository extends JpaRepository<ComboServicioDetalle, String> {
    java.util.List<ComboServicioDetalle> findByComboServicioId(String comboServicioId);
    void deleteByComboServicioId(String comboServicioId);
}
