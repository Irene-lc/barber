package edu.upb.barber.repository;

import edu.upb.barber.repository.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, String> {

    //Metodo para que podamos buscar el pago cuando llega nuestro webhook
    Optional<Pago> findByTransaccionExternaId(String transaccionExternaId);
}

