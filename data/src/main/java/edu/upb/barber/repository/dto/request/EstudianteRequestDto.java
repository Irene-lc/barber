package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.upb.barber.repository.entity.Materia;
import edu.upb.barber.repository.entity.enums.RolUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EstudianteRequestDto {
    @JsonProperty("nro_telefono")
    private String nroTelefono;
    @JsonProperty("nro_documento")
    private String nroDocumento;

}