package edu.upb.barber.service.integracion.stereum;

import edu.upb.barber.service.exception.NotDataFoundException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Slf4j
@Service
public class StereumPayClient {

    @Value("${stereum.url-base}")
    private String urlBase;

    @Value("${stereum.api-key}")
    private String apiKey;

    @Value("${stereum.connect-timeout:10000}")
    private int connectTimeout;

    @Value("${stereum.read-timeout:40000}")
    private int readTimeout;

    public StereumChargeResponseDto createCharge(StereumChargeRequestDto request) throws Exception {
        RestClient restClient = create();

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("country", request.getCountry());
        jsonObject.put("amount", request.getAmount());
        jsonObject.put("currency", request.getCurrency());
        jsonObject.put("network", request.getNetwork());
        jsonObject.put("idempotency_key", request.getIdempotencyKey());
        jsonObject.put("charge_reason", request.getChargeReason());
        jsonObject.put("reservation_validity_time", request.getReservationValidityTime());
        JSONObject customerJson = new JSONObject();
        customerJson.put("name", request.getCustomer().getName());
        customerJson.put("lastname", request.getCustomer().getLastname());
        customerJson.put("document_number", request.getCustomer().getDocumentNumber());
        jsonObject.put("customer", customerJson);

//        ResponseEntity<StereumChargeResponseDto> response;
        ResponseEntity<String> response;


        try {
            response = restClient.post()
                    .uri(urlBase + "/api/v1/transactions/create-charge")
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                    .header("x-api-key", apiKey)
//                    .body(request)
                    .body(jsonObject.toString())
                    .retrieve()
//                    .toEntity(StereumChargeResponseDto.class);
                    .toEntity(String.class);
        } catch (Exception e) {
            log.error("Exception in Stereum createCharge: ", e);
            throw e;
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new Exception("Error al generar el cobro QR. Status: " + response.getStatusCode().value());
        }

        JSONObject jsonResponse = new JSONObject(response.getBody());
        String pagoId = jsonResponse.getString("id");
        StereumChargeResponseDto responseDto = new StereumChargeResponseDto();
        responseDto.setId(pagoId);

//        return response.getBody();
        return responseDto;
    }

    public StereumVerifyResponseDto verifyPayment(String transactionId) throws Exception {
        RestClient restClient = create();
        ResponseEntity<StereumVerifyResponseDto> response;

        try {
            response = restClient.get()
                    .uri(urlBase + "/api/v1/transactions/" + transactionId + "/verify")
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                    .header("x-api-key", apiKey)
                    .retrieve()
                    .toEntity(StereumVerifyResponseDto.class);
        } catch (Exception e) {
            log.error("Exception in Stereum verifyPayment: ", e);
            throw e;
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new Exception("Error al verificar estado de pago. Status: " + response.getStatusCode().value());
        }

        return response.getBody();
    }

    private RestClient create() {
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(Duration.ofMillis(connectTimeout));
        clientHttpRequestFactory.setReadTimeout(Duration.ofMillis(readTimeout));
        return RestClient.builder().requestFactory(clientHttpRequestFactory).build();
    }
}
