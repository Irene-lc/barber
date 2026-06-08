package edu.upb.barber.service;

import edu.upb.barber.repository.PagoRepository;
import edu.upb.barber.repository.VentaRepository;
import edu.upb.barber.repository.UsuarioRepository;
import edu.upb.barber.repository.dto.request.GenerarPagoRequestDto;
import edu.upb.barber.repository.dto.request.NotificacionRequestDto;
import edu.upb.barber.repository.dto.request.PagoRequestDto;
import edu.upb.barber.repository.dto.response.GenerarPagoResponseDto;
import edu.upb.barber.repository.dto.response.PagoResponseDto;
import edu.upb.barber.repository.entity.Cliente;
import edu.upb.barber.repository.entity.Pago;
import edu.upb.barber.repository.entity.Venta;
import edu.upb.barber.repository.entity.Usuario;
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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final VentaRepository ventaRepository;
    private final UsuarioRepository usuarioRepository;
    private final StereumPayClient stereumPayClient;

    @Transactional
    public GenerarPagoResponseDto generarCobroQR(GenerarPagoRequestDto request) throws Exception {
        Venta venta = ventaRepository.findById(request.getVentaId())
                .orElseThrow(() -> new Exception("Venta no encontrada con ID: " + request.getVentaId()));

        Cliente cliente = venta.getCliente();
        if (cliente == null) {
            throw new Exception("La venta no tiene un cliente asociado");
        }

        StereumCustomerDto customerDto = new StereumCustomerDto();
        customerDto.setName(cliente.getNombre());
        customerDto.setLastname("");
        customerDto.setDocumentNumber(cliente.getDocumento() != null ? cliente.getDocumento() : "000000");

        StereumChargeRequestDto chargeRequest = new StereumChargeRequestDto();
        chargeRequest.setCountry("BO");
        chargeRequest.setAmount(venta.getTotal().toString());
        chargeRequest.setCurrency("USDT");
        chargeRequest.setNetwork("POLYGON");
        chargeRequest.setIdempotencyKey(UUID.randomUUID().toString());
        chargeRequest.setChargeReason("Cobro de barberia Venta: " + venta.getId());
        chargeRequest.setReservationValidityTime("15");
        chargeRequest.setCustomer(customerDto);

        log.info("Llamando a Stereum para generar QR por un monto de {}", chargeRequest.getAmount());
        StereumChargeResponseDto stereumResponse = stereumPayClient.createCharge(chargeRequest);

        Pago pago = new Pago();
        pago.setVenta(venta);
        pago.setMonto(venta.getTotal());
        pago.setMetodoPago(MetodoPago.TRANSFERENCIA);
        pago.setEstadoPago(EstadoPago.PENDIENTE);
        pago.setTransaccionExternaId(stereumResponse.getId());

        pago = pagoRepository.save(pago);

        GenerarPagoResponseDto responseDto = new GenerarPagoResponseDto();
        responseDto.setPagoId(pago.getId());
        responseDto.setQrBase64(stereumResponse.getQrBase64());
        responseDto.setPaymentLink(stereumResponse.getPaymentLink());

        return responseDto;
    }

    /**
     * Procesa la notificacion del webhook
     * Busca el pago por su ID externo y actualiza su estado segun el resultado
     * Estados de Stereum: COMPLETED, FAILED, EXPIRED, PENDING
     */
    @Transactional
    public void procesarNotificacionWebhook(NotificacionRequestDto notificacion) throws Exception {
        if (notificacion.getTransaction() == null || notificacion.getTransaction().getId() == null) {
            log.warn("Notificacion de Stereum recibida sin datos de transaccion: {}", notificacion);
            return;
        }

        String transaccionExternaId = notificacion.getTransaction().getId().toString();
        String statusRecibido = notificacion.getTransaction().getStatus();

        log.info("Procesando notificacion webhook de Stereum. TransaccionId={}, Status={}",
                transaccionExternaId, statusRecibido);

        Pago pago = pagoRepository.findByTransaccionExternaId(transaccionExternaId)
                .orElseThrow(() -> new Exception(
                        "No se encontro un Pago con transaccionExternaId: " + transaccionExternaId));

        EstadoPago nuevoEstado = mapearEstadoStereum(statusRecibido);
        log.info("Actualizando Pago id={} de estado {} a {}", pago.getId(), pago.getEstadoPago(), nuevoEstado);

        pago.setEstadoPago(nuevoEstado);

        if (nuevoEstado == EstadoPago.PAGADO) {
            pago.setPagadoEn(OffsetDateTime.now());
        }

        pagoRepository.save(pago);
        log.info("Pago id={} actualizado exitosamente a estado {}", pago.getId(), nuevoEstado);
    }


    // Mapea el status de Stereum al enum interno EstadoPago.

    private EstadoPago mapearEstadoStereum(String statusStereum) {
        if (statusStereum == null) {
            return EstadoPago.PENDIENTE;
        }
        return switch (statusStereum.toUpperCase()) {
            case "COMPLETED" -> EstadoPago.PAGADO;
            case "FAILED", "EXPIRED" -> EstadoPago.ANULADO;
            default -> EstadoPago.PENDIENTE;
        };
    }

    @Transactional
    public PagoResponseDto crear(PagoRequestDto request) throws Exception {
        Venta venta = ventaRepository.findById(request.getVentaId())
                .orElseThrow(() -> new Exception("Venta no encontrada con ID: " + request.getVentaId()));

        Usuario usuario = null;
        if (request.getRegistradoPorUsuarioId() != null && !request.getRegistradoPorUsuarioId().isBlank()) {
            usuario = usuarioRepository.findById(request.getRegistradoPorUsuarioId())
                    .orElseThrow(() -> new Exception("Usuario no encontrado con ID: " + request.getRegistradoPorUsuarioId()));
        }

        Pago pago = new Pago();
        pago.setVenta(venta);
        pago.setMonto(request.getMonto());
        pago.setMetodoPago(request.getMetodoPago());
        pago.setEstadoPago(request.getEstadoPago() != null ? request.getEstadoPago() : EstadoPago.PENDIENTE);
        pago.setPagadoEn(request.getPagadoEn());
        pago.setRegistradoPorUsuario(usuario);
        pago.setTransaccionExternaId(request.getTransaccionExternaId());

        pago = pagoRepository.save(pago);
        return new PagoResponseDto(pago);
    }

    @Transactional
    public PagoResponseDto update(String id, PagoRequestDto request) throws Exception {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new Exception("Pago no encontrado con ID: " + id));

        Venta venta;
        if (request.getVentaId() != null) {
            venta = ventaRepository.findById(request.getVentaId())
                    .orElseThrow(() -> new Exception("Venta no encontrada con ID: " + request.getVentaId()));
        } else {
            // Si el frontend no envia venta_id, conservamos la venta que el pago ya tenia
            venta = pago.getVenta();
        }

        Usuario usuario = null;
        if (request.getRegistradoPorUsuarioId() != null && !request.getRegistradoPorUsuarioId().isBlank()) {
            usuario = usuarioRepository.findById(request.getRegistradoPorUsuarioId())
                    .orElseThrow(() -> new Exception("Usuario no encontrado con ID: " + request.getRegistradoPorUsuarioId()));
        }

        pago.setVenta(venta);
        pago.setMonto(request.getMonto());
        pago.setMetodoPago(request.getMetodoPago());
        pago.setEstadoPago(request.getEstadoPago());
        pago.setPagadoEn(request.getPagadoEn());
        pago.setRegistradoPorUsuario(usuario);
        pago.setTransaccionExternaId(request.getTransaccionExternaId());

        pago = pagoRepository.save(pago);
        return new PagoResponseDto(pago);
    }

    @Transactional
    public void delete(String id) throws Exception {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new Exception("Pago no encontrado con ID: " + id));
        pagoRepository.delete(pago);
    }

    @Transactional(readOnly = true)
    public List<PagoResponseDto> listar() {
        return pagoRepository.findAll().stream()
                .map(PagoResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<PagoResponseDto> findById(String id) {
        return pagoRepository.findById(id).map(PagoResponseDto::new);
    }
}
