package edu.upb.barber.job;

import edu.upb.barber.quartz.service.JobDto;
import edu.upb.barber.repository.VentaRepository;
import edu.upb.barber.repository.entity.Venta;
import edu.upb.barber.repository.entity.enums.EstadoVenta;
import edu.upb.barber.service.EmailService;
import edu.upb.barber.service.VentaService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class PedidoCanceladoJob extends QuartzJobBean implements InterruptableJob {
    public static final String NAME_JOB = "PedidoCanceladoJob";
    private static final String NAME_TRIGGER = "PedidoCanceladoJob-trigger";

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private VentaService ventaService;

    @Autowired
    private EmailService emailService;

    @SneakyThrows
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Ejecutando verificación de pedidos no pagados");
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(1);
        
        List<Venta> pedidosExpirados = ventaRepository.findByEstadoAndCreatedDateBefore(
                EstadoVenta.ABIERTA, threshold);

        if (!pedidosExpirados.isEmpty()) {
            log.info("Se encontraron pedidos expirados para cancelar.");
            for (Venta venta : pedidosExpirados) {
                try {
                    ventaService.cancelarVentaAutomatica(venta);
                    emailService.sendPedidoCancelado("rllayus@gmail.com", "Pregunta 6-A", "Pedido Cancelado");
                } catch (Exception e) {
                    log.error("Error al cancelar venta");
                }
            }
        }
    }

    public static JobDto getJobDto(String groupName) {
        JobDto jobDto = new JobDto();
        jobDto.setGroupName(groupName);
        jobDto.setJobName(NAME_JOB);
        jobDto.setTriggerKey(NAME_TRIGGER);
        jobDto.setJobClass(PedidoCanceladoJob.class);
        return jobDto;
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        log.info("Deteniendo el hilo de ejecución.");
    }
}

