package edu.upb.barber.service;

import edu.upb.barber.repository.ClienteRepository;
import edu.upb.barber.repository.SucursalRepository;
import edu.upb.barber.repository.VentaRepository;
import edu.upb.barber.repository.dto.request.VentaRequestDto;
import edu.upb.barber.repository.entity.Cliente;
import edu.upb.barber.repository.entity.Sucursal;
import edu.upb.barber.repository.entity.Venta;
import edu.upb.barber.repository.entity.enums.EstadoVenta;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AllArgsConstructor
@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final SucursalRepository sucursalRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public Venta crear(VentaRequestDto request) throws Exception {

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new Exception("Sucursal no encontrada con ID: " + request.getSucursalId()));

        Cliente cliente = null;
        if (request.getClienteId() != null) {
            cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new Exception("Cliente no encontrado con ID: " + request.getClienteId()));
        }

        Venta venta = new Venta();
        venta.setSucursal(sucursal);
        venta.setCliente(cliente);
        venta.setSubtotal(request.getSubtotal());
        venta.setDescuento(request.getDescuento());
        venta.setTotal(request.getTotal());
        venta.setNotas(request.getNotas());
        venta.setEstado(EstadoVenta.ABIERTA); // Por defecto abierta hasta que se pague

        return ventaRepository.save(venta);
    }

    @Transactional(readOnly = true)
    public java.util.List<Venta> listar() {
        return ventaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public java.util.Optional<Venta> findById(String id) {
        return ventaRepository.findById(id);
    }
}
