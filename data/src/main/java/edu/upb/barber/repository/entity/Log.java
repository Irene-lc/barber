package edu.upb.barber.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.boot.logging.LogLevel;

@Builder
@AllArgsConstructor
@Entity
@NoArgsConstructor
@Table(name = "logs")
public class Log {
    @Id
    @UuidGenerator
    private String id;

    @Column(name = "nivel",length = 10, comment = "Esta columna almacena el nivel del log")
    @Enumerated(EnumType.STRING)
    private LogLevel level;

    @Column(name = "message", length = 4000, comment = "Esta columna almacena la descripcion del log")
    private String message;


}
