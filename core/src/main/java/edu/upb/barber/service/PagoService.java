package edu.upb.barber.service;
import edu.upb.barber.repository.PagoRepository;
import edu.upb.barber.repository.VentaRepository;
import edu.upb.barber.repository.dto.request.PagoRequestDto;
import edu.upb.barber.repository.dto.response.Customer;
import edu.upb.barber.repository.dto.request.StereumDto;
import edu.upb.barber.repository.entity.Pago;
import edu.upb.barber.repository.entity.enums.EstadoPago;
import edu.upb.barber.repository.entity.enums.MetodoPago;
import edu.upb.barber.service.integracion.SistemaA;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class PagoService {

    private final SistemaA sistemaA;
    private final PagoRepository pagoRepository;
    private final VentaRepository ventaRepository;

    private static final String STEREUM_API_KEY = "a9693a49-4061-4ea8-b2af-3fbb55ece15f";

    public StereumDto crearCargo(PagoRequestDto request) throws Exception {
        StereumDto stereumRequest = StereumDto.builder()
                .country("BO")
                .amount(request.getMonto().toPlainString())
                .currency("USDT")
                .network("POLYGON")
                .idempotencyKey(UUID.randomUUID().toString())
                .chargeReason("Pago de servicio")
                .reservationValidityTime("10")
                .customer(Customer.builder()
                        .name(request.getUsuario().getNombre())
                        .lastname(request.getUsuario().getApellido())
                        .documentNumber(request.getUsuario().getUsername())
                        .build())
                .build();

        StereumDto stereumResponse = sistemaA.consumirStereum(STEREUM_API_KEY, stereumRequest);

        var venta = ventaRepository.findById(request.getVentaId())
                .orElseThrow(() -> new Exception("Venta no encontrada: " + request.getVentaId()));

        Pago pago = new Pago();
        pago.setVenta(venta);
        pago.setMonto(request.getMonto());
        pago.setMetodoPago(MetodoPago.QR);
        pago.setEstadoPago(EstadoPago.PENDIENTE);
        pagoRepository.save(pago);

        return stereumResponse;
    }
}