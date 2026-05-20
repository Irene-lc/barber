package edu.upb.barber.repository.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComboServicioDetalleRequest {
    private String comboServicioId;
    private String servicioId;
    private int ordenEjecucion = 1;
}
