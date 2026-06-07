package edu.upb.barber.repository.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificacionRequestDto {

    @JsonProperty("notification_type")
    private String notificationType;
    private String id;
    private TransactonRequestDto transaction;
    private Long timestamp;

}