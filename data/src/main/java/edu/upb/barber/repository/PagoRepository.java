package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Pago;
import edu.upb.barber.repository.entity.enums.EstadoPago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, String> {

    //Metodo para que podamos buscar el pago cuando llega nuestro webhook
    Optional<Pago> findByTransaccionExternaId(String transaccionExternaId);
    @Query("""
    SELECT p FROM Pago p
    LEFT JOIN p.venta v
    LEFT JOIN v.cliente c
    WHERE (:nombre IS NULL OR c.nombre LIKE '%' || :nombre || '%')
    ORDER BY p.createdDate DESC
    """)
    Page<Pago> findByNombre(
            @Param("nombre") String nombre,
            Pageable pageable
    );

    @Modifying
    @Query("""
    UPDATE Pago p
    SET p.estadoPago = :nuevoEstado,
        p.pagadoEn = CASE WHEN :nuevoEstado = 'PAGADO'
                    THEN CURRENT_TIMESTAMP ELSE p.pagadoEn END
    WHERE p.id = :pagoId
    """)
    int actualizarEstadoPago(
            @Param("pagoId") String pagoId,
            @Param("nuevoEstado") EstadoPago nuevoEstado
    );


}

