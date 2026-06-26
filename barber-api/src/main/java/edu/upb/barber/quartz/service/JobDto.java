package edu.upb.barber.quartz.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.Serializable;
import java.util.Date;


@Getter
@Setter
public class JobDto implements Serializable {

    private String groupName;
    private String jobName;
    private String triggerName;
    private Date scheduleTime;
    private Date lastFiredTime;
    private Date nextFireTime;
    private String jobStatus;
    private String description;
    private String cronExpression;
    private String triggerKey;
    private Class<? extends QuartzJobBean> jobClass;
}
