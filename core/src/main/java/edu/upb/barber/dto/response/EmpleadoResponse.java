package edu.upb.barber.dto.response;

import edu.upb.barber.repository.entity.Empleado;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EmpleadoResponse {
    private String id;
    private String empresaId;
    private String empresaNombre;
    private String usuarioId;
    private String nombre;
    private String telefono;
    private String email;
    private String cargo;
    private String especialidad;
    private BigDecimal porcentajeComision;
    private BigDecimal pagoFijoMensual;
    private String fotoUrl;
    private boolean disponible;
    private boolean activo;

    public static EmpleadoResponse fromEntity(Empleado e) {
        EmpleadoResponse dto = new EmpleadoResponse();
        dto.id = e.getId();
        if (e.getEmpresa() != null) {
            dto.empresaId = e.getEmpresa().getId();
            dto.empresaNombre = e.getEmpresa().getNombre();
        }
        if (e.getUsuario() != null) {
            dto.usuarioId = e.getUsuario().getId();
        }
        dto.nombre = e.getNombre();
        dto.telefono = e.getTelefono();
        dto.email = e.getEmail();
        dto.cargo = e.getCargo();
        dto.especialidad = e.getEspecialidad();
        dto.porcentajeComision = e.getPorcentajeComision();
        dto.pagoFijoMensual = e.getPagoFijoMensual();
        dto.fotoUrl = e.getFotoUrl();
        dto.disponible = e.isDisponible();
        dto.activo = e.isActivo();
        return dto;
    }
}
