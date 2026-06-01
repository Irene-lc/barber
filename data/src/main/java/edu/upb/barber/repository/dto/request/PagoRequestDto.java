package edu.upb.barber.repository.dto.request;

import edu.upb.barber.repository.entity.Usuario;
import edu.upb.barber.repository.entity.enums.EstadoPago;
import edu.upb.barber.repository.entity.enums.MetodoPago;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PagoRequestDto {
    private EstadoPago estadoPago;
    private MetodoPago metodoPago;
    private BigDecimal monto;
    private String ventaId;
    private Usuario usuario;

}