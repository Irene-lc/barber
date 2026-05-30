package edu.upb.barber.service;

import edu.upb.barber.repository.PagoRepository;
import edu.upb.barber.repository.VentaRepository;
import edu.upb.barber.repository.dto.request.GenerarPagoRequestDto;
import edu.upb.barber.repository.dto.response.GenerarPagoResponseDto;
import edu.upb.barber.repository.entity.Cliente;
import edu.upb.barber.repository.entity.Pago;
import edu.upb.barber.repository.entity.Venta;
import edu.upb.barber.repository.entity.enums.EstadoPago;
import edu.upb.barber.repository.entity.enums.MetodoPago;
import edu.upb.barber.service.integracion.stereum.StereumChargeRequestDto;
import edu.upb.barber.service.integracion.stereum.StereumChargeResponseDto;
import edu.upb.barber.service.integracion.stereum.StereumCustomerDto;
import edu.upb.barber.service.integracion.stereum.StereumPayClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final VentaRepository ventaRepository;
    private final StereumPayClient stereumPayClient;

    @Transactional
    public GenerarPagoResponseDto generarCobroQR(GenerarPagoRequestDto request) throws Exception {
        // 1. Buscar la venta
        Venta venta = ventaRepository.findById(request.getVentaId())
                .orElseThrow(() -> new Exception("Venta no encontrada con ID: " + request.getVentaId()));

        Cliente cliente = venta.getCliente();
        if (cliente == null) {
            throw new Exception("La venta no tiene un cliente asociado");
        }

        // 2. Preparar el Customer DTO para Stereum
        StereumCustomerDto customerDto = new StereumCustomerDto();
        customerDto.setName(cliente.getNombre());
        customerDto.setLastname(""); // Lo dejamos vacío como acordamos
        customerDto.setDocumentNumber(cliente.getDocumento() != null ? cliente.getDocumento() : "000000");

        // 3. Armar el request para Stereum
        StereumChargeRequestDto chargeRequest = new StereumChargeRequestDto();
        chargeRequest.setCountry("BO");
        chargeRequest.setAmount(venta.getTotal().toString()); // Mandamos el total exacto
        chargeRequest.setCurrency("USDT");
        chargeRequest.setNetwork("POLYGON");
        chargeRequest.setIdempotencyKey(UUID.randomUUID().toString());
        chargeRequest.setChargeReason("Cobro de barbería Venta: " + venta.getId());
        chargeRequest.setReservationValidityTime("15"); // 15 minutos para pagar
        chargeRequest.setCustomer(customerDto);

        // 4. Llamar a Stereum (Conexión real)
        log.info("Llamando a Stereum para generar QR por un monto de {}", chargeRequest.getAmount());
        StereumChargeResponseDto stereumResponse = stereumPayClient.createCharge(chargeRequest);

        // 5. Crear el Pago en tu Base de Datos
        Pago pago = new Pago();
        pago.setVenta(venta);
        pago.setMonto(venta.getTotal());
        pago.setMetodoPago(MetodoPago.TRANSFERENCIA); // Ajusta según tus Enum, TRANSFERENCIA o similar
        pago.setEstadoPago(EstadoPago.PENDIENTE);
        pago.setTransaccionExternaId(stereumResponse.getId()); // ID real del pago en Stereum
        
        pago = pagoRepository.save(pago);

        // 6. Retornar el QR al frontend o postman
        GenerarPagoResponseDto responseDto = new GenerarPagoResponseDto();
        responseDto.setPagoId(pago.getId());
        responseDto.setQrBase64(stereumResponse.getQrBase64());
        responseDto.setPaymentLink(stereumResponse.getPaymentLink());

        return responseDto;
    }
}
