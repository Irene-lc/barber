package edu.upb.barber;

import edu.upb.barber.service.integracion.stereum.StereumChargeRequestDto;
import edu.upb.barber.service.integracion.stereum.StereumChargeResponseDto;
import edu.upb.barber.service.integracion.stereum.StereumCustomerDto;
import edu.upb.barber.service.integracion.stereum.StereumPayClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class StereumTestRunner implements CommandLineRunner {

    private final StereumPayClient stereumPayClient;

    @Override
    public void run(String... args) throws Exception {
        log.info(" INICIANDO PRUEBA DE CONEXIÓN CON STEREUM PAY ");

        try {
            // 1. Armamos un cliente de prueba
            StereumCustomerDto customer = new StereumCustomerDto();
            customer.setName("Prueba");
            customer.setLastname("Consola");
            customer.setDocumentNumber("12345678");

            // 2. Armamos el request de prueba por 1 USDT
            StereumChargeRequestDto request = new StereumChargeRequestDto();
            request.setCountry("BO");
            request.setAmount("1"); 
            request.setCurrency("USDT");
            request.setNetwork("POLYGON");
            request.setIdempotencyKey(UUID.randomUUID().toString());
            request.setChargeReason("Prueba desde Consola Spring Boot");
            request.setReservationValidityTime("10");
            request.setCustomer(customer);

            log.info("Enviando petición a Stereum...");
            
            // 3. Ejecutamos la llamada HTTP
            StereumChargeResponseDto response = stereumPayClient.createCharge(request);

            log.info(" CONEXIÓN EXITOSA!");
            log.info(" ID Transacción : {}", response.getId());
            log.info(" Link de Pago   : {}", response.getPaymentLink());
            log.info(" QR (Base64)    : {}...", response.getQrBase64().substring(0, Math.min(30, response.getQrBase64().length()))); // Solo imprimimos un pedacito del base64 para no ensuciar toda la consola

        } catch (Exception e) {
            log.error("ERROR EN LA CONEXIÓN CON STEREUM: ", e);
        }
    }
}
