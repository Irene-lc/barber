package edu.upb.barber.repository.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.Empleado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmpleadoResponseDto {

    private String id;
    private String nombre;
    private String cargo;
    private String telefono;
    private String email;
    private String especialidad;

    @JsonProperty("foto_url")
    private String fotoUrl;

    @JsonProperty("empresa_id")
    private String empresaId;

    @JsonProperty("empresa_nombre")
    private String empresaNombre;

    @JsonProperty("usuario_id")
    private String usuarioId;

    @JsonProperty("usuario_nombre")
    private String usuarioNombre;

    private boolean disponible;
    private boolean activo;

    public EmpleadoResponseDto(Empleado empleado) {
        this.id = empleado.getId();
        this.nombre = empleado.getNombre();
        this.cargo = empleado.getCargo() != null ? empleado.getCargo().name() : null;
        this.telefono = empleado.getTelefono();
        this.email = empleado.getEmail();
        this.especialidad = empleado.getEspecialidad();
        this.fotoUrl = empleado.getFotoUrl();
        if (empleado.getEmpresa() != null) {
            this.empresaId = empleado.getEmpresa().getId();
            this.empresaNombre = empleado.getEmpresa().getNombre();
        }
        if (empleado.getUsuario() != null) {
            this.usuarioId = empleado.getUsuario().getId();
            this.usuarioNombre = empleado.getUsuario().getNombre();
        }
        this.disponible = empleado.isDisponible();
        this.activo = empleado.isActivo();
    }
}
