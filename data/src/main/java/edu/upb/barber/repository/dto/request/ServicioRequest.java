package edu.upb.barber.repository.dto.request;

import edu.upb.barber.repository.entity.enums.CategoriaServicio;
import edu.upb.barber.repository.entity.enums.TipoDestinatarioServicio;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ServicioRequest {
    private String empresaId;
    private String nombre;
    private String descripcion;
    private TipoDestinatarioServicio destinatario;
    private CategoriaServicio categoria;
    private int duracionMinutos;
    private BigDecimal precioBase;
    private boolean activo = true;
}
