package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data

public class NotificacionRequestDto {

        @JsonProperty("notification_type")
        private String notificationType;

        private UUID id;

        private TransaccionRequestDto transaction;

        private long timestamp;
}
