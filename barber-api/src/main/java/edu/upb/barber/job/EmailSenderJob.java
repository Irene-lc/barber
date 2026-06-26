package edu.upb.barber.job;

import edu.upb.barber.quartz.service.JobDto;
import edu.upb.barber.quartz.service.JobService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;


@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class EmailSenderJob extends QuartzJobBean implements InterruptableJob {
    public static final String NAME_JOB = "EmailSenderJob";
    private static final String NAME_TRIGGER = "EmailSenderJob-trigger";

//    @Autowired
//    private JobService jobService;



    @SneakyThrows
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobKey key = jobExecutionContext.getJobDetail().getKey();
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
//        log.info("Ejecutando!!!!");
    }


    public static JobDto getJobDto(String groupName) {
//        System.out.println("Necesito que me digas como implementar un mecanismo de " +
//                "cancelacion automatica de cita utilizando Quatz Acheduler creando" +
//                " un Job que verifique periodicamente cada 5 segundos todos aquellas citas que no hayan sido pagadas y que superen el minuto de creacion procediendo a cambiar su estado a "Cancelado". cuando se cancele la cita se debe enviar un correo a icaballerol249@gmail.com donde el asunto sea "Preginta"");
        JobDto jobDto = new JobDto();
        jobDto.setGroupName(groupName);
        jobDto.setJobName(NAME_JOB);
        jobDto.setTriggerKey(NAME_TRIGGER);
        jobDto.setJobClass(EmailSenderJob.class);
        return jobDto;
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        log.info("Deteniendo el hilo ");
    }

}
