package edu.upb.barber.job;

import edu.upb.barber.quartz.service.JobDto;
import edu.upb.barber.repository.VentaRepository;
import edu.upb.barber.repository.entity.Venta;
import edu.upb.barber.repository.entity.enums.EstadoVenta;
import edu.upb.barber.service.EmailService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Service
public class CancelacionPedidosJob extends QuartzJobBean implements InterruptableJob {
    public static final String NAME_JOB = "CancelacionPedidosJob";
    private static final String NAME_TRIGGER = "CancelacionPedidosJob-trigger";
    @Autowired
    private VentaRepository ventaRepository;
    @Autowired
    private EmailService emailService;

    @SneakyThrows
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Ejecutando Job de Cancelación de pedidos");
        LocalDateTime limite = LocalDateTime.now().minusMinutes(1);
        List<Venta> pedidosNoPagados = ventaRepository.findByEstadoAndCreatedDateBefore(EstadoVenta.ABIERTA, limite);

        for (Venta venta : pedidosNoPagados) {
            log.info("Cancelando venta con id: " + venta.getId());
            venta.setEstado(EstadoVenta.ANULADA);
            ventaRepository.save(venta);
            emailService.cancelacionPedido(
                    "adriana.bauer11@gmail.com",
                    "Adriana",
                    "Pregunta 6-A",
                    "Pedido cancelado"
            );
        }
    }

    public static JobDto getJobDto(String groupName) {
        JobDto jobDto = new JobDto();
        jobDto.setGroupName(groupName);
        jobDto.setJobName(NAME_JOB);
        jobDto.setTriggerKey(NAME_TRIGGER);
        jobDto.setJobClass(CancelacionPedidosJob.class);
        return jobDto;
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        log.info("Deteniendo el hilo de CancelacionPedidosJob");
    }
}
