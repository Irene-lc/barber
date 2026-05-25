package edu.upb.barber.repository.dto.response;

import edu.upb.barber.repository.entity.Estudiante;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstudianteResponseDto {
    private String id;
    private String nombre;
    private String apellido;
    @JsonProperty("materia_id")
    private String materiaId;
    private int nota;
    @JsonProperty("nro_telefono")
    private String nroTelefono;
    @JsonProperty("nro_documento")
    private String nroDocumento;

    public EstudianteResponseDto(Estudiante e) {
        this.id = e.getId();
        this.nombre = e.getNombre();
        this.apellido = e.getApellido();
        if (e.getMateria() != null) {
            this.materiaId = e.getMateria().getId();
        }
        this.nota = e.getNota();
        this.nroTelefono = e.getNroTelefono();
        this.nroDocumento = e.getNroDocumento();
    }
}
