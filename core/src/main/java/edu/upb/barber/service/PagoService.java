package edu.upb.barber.service;

import edu.upb.barber.repository.PagoRepository;
import edu.upb.barber.repository.VentaRepository;
import edu.upb.barber.repository.UsuarioRepository;
import edu.upb.barber.repository.dto.request.GenerarPagoRequestDto;
import edu.upb.barber.repository.dto.request.PagoRequestDto;
import edu.upb.barber.repository.dto.response.GenerarPagoResponseDto;
import edu.upb.barber.repository.dto.response.PagoResponseDto;
import edu.upb.barber.repository.entity.Cliente;
import edu.upb.barber.repository.entity.Pago;
import edu.upb.barber.repository.entity.Venta;
import edu.upb.barber.repository.entity.Usuario;
import edu.upb.barber.repository.entity.enums.EstadoPago;
import edu.upb.barber.repository.entity.enums.EstadoVenta;
import edu.upb.barber.repository.entity.enums.MetodoPago;
import edu.upb.barber.service.integracion.stereum.StereumChargeRequestDto;
import edu.upb.barber.service.integracion.stereum.StereumChargeResponseDto;
import edu.upb.barber.service.integracion.stereum.StereumCustomerDto;
import edu.upb.barber.service.integracion.stereum.StereumPayClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

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
    private final ObjectMapper objectMapper;

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
        chargeRequest.setChargeReason("Cobro de barbería Venta: " + venta.getId());
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

    @Transactional
    public void procesarNotificacionStereum(String body) throws Exception {
        JsonNode root = objectMapper.readTree(body);
        String notificationType = text(root, "notificationType", "notification_type", "type");

        if ("webhooks.test".equals(notificationType)) {
            log.info("Webhook de prueba recibido desde Stereum");
            return;
        }

        String transactionId = text(root, "transactionId", "transaction_id", "id");
        if (transactionId == null && root.has("transaction")) {
            transactionId = text(root.get("transaction"), "id", "transactionId", "transaction_id");
        }
        if (transactionId == null && root.has("data")) {
            transactionId = text(root.get("data"), "id", "transactionId", "transaction_id");
        }

        if (transactionId == null || transactionId.isBlank()) {
            log.warn("Webhook Stereum sin transaction id. notificationType={}, body={}", notificationType, body);
            return;
        }

        Optional<Pago> pagoOptional = pagoRepository.findByTransaccionExternaId(transactionId);
        if (pagoOptional.isEmpty()) {
            log.warn("Webhook Stereum para transaccion no registrada: {}", transactionId);
            return;
        }

        Pago pago = pagoOptional.get();
        String status = text(root, "status", "state", "transactionStatus", "transaction_status");
        if (status == null && root.has("transaction")) {
            status = text(root.get("transaction"), "status", "state", "transactionStatus", "transaction_status");
        }
        if (status == null && root.has("data")) {
            status = text(root.get("data"), "status", "state", "transactionStatus", "transaction_status");
        }

        if (esPagoConfirmado(notificationType, status)) {
            pago.setEstadoPago(EstadoPago.PAGADO);
            pago.setPagadoEn(OffsetDateTime.now());
            pago.getVenta().setEstado(EstadoVenta.COBRADA);
            pagoRepository.save(pago);
            log.info("Pago {} confirmado por webhook Stereum. Transaccion {}", pago.getId(), transactionId);
            return;
        }

        log.info("Webhook Stereum recibido sin confirmacion de pago. transactionId={}, notificationType={}, status={}",
                transactionId, notificationType, status);
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

        Venta venta = ventaRepository.findById(request.getVentaId())
                .orElseThrow(() -> new Exception("Venta no encontrada con ID: " + request.getVentaId()));

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

    private boolean esPagoConfirmado(String notificationType, String status) {
        if (status == null || status.isBlank()) {
            return "transactions.outbound".equals(notificationType) || "transactions.inbound".equals(notificationType);
        }

        String normalized = status.trim().toUpperCase();
        return normalized.equals("PAID")
                || normalized.equals("PAGADO")
                || normalized.equals("COMPLETED")
                || normalized.equals("CONFIRMED")
                || normalized.equals("SUCCESS")
                || normalized.equals("APPROVED");
    }

    private String text(JsonNode node, String... fieldNames) {
        if (node == null) {
            return null;
        }
        for (String fieldName : fieldNames) {
            JsonNode value = node.get(fieldName);
            if (value != null && !value.isNull()) {
                return value.asText();
            }
        }
        return null;
    }
}
